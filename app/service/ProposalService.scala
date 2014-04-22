package service

import akka.actor._

import datasource.user._
import datasource.calendar._
import datasource.appointmentproposal._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag =>_, _}

import service.protocol._

import util._

/**
 *
 * @author Simon Kaltenbacher
 */
object ProposalService {

  def props(db: Database): Props = Props(classOf[ProposalService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  * @author Florian Liebhart
  */
class ProposalService(db: Database)
  extends Actor with 
          ActorLogging with
          UserDataAccessComponentImpl with
          CalendarDataAccessComponentImpl with
          AppointmentProposalDataAccessComponentImpl with
          ExceptionHandling {

  /** */
  protected object userDataAccess extends UserDataAccessModuleImpl

  /** */
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl

  protected object appointmentProposalDataAccess extends AppointmentProposalDataAccessModuleImpl

  import calendarDataAccess._
  import appointmentProposalDataAccess._
  import userDataAccess._

  /*
   * Proposal
   */

  def addProposal(msg: AddProposal) = db.withSession { implicit session =>
    sender ! ProposalAdded((proposals returning proposals.map(_.id)) += Proposal(-1, msg.title, msg.userId))
  }

  def addProposalTime(msg: AddProposalTime) = 
    sender ! ProposalTimeAdded(db.withTransaction { implicit session =>
      val proposalTimeId = (proposalTimes returning proposalTimes.map(_.id)) += ProposalTime(-1, msg.start, msg.end, msg.proposalId)
      proposalTimeVotes ++= msg.participants.map(ProposalTimeVote(proposalTimeId, _, Vote.NotVoted))
      proposalTimeId
  })

  def addProposalTimeVote(msg: AddProposalTimeVote) = db.withSession { implicit session =>
    proposalTimeVotes
      .filter(_.proposalTimeId === msg.proposalTimeId)
      .filter(_.userId === msg.userId)
      .map(v => (v.vote))
      .update((msg.vote))
    sender ! ProposalTimeVoteAdded
  }

  def getProposalsFromUser(msg: GetProposalsFromUser) = db.withTransaction { implicit session =>
    val proposals = proposalsFromUser(msg.userId).buildColl[Seq].distinct
    val times =
      proposalTimes
        .filter(_.id.inSet(proposals.map(_.id)))
        .buildColl[Seq]
        .distinct
        .groupBy(_.proposalId)

    sender ! ProposalsFromUser(proposals.map({ proposal =>
      val ptv =
        times(proposal.id).map({ time =>
          val votes =
            (for {
              ptv <- proposalTimeVotes
              if (ptv.proposalTimeId === time.id)
              u <- users
              if (u.id === ptv.userId)
            } yield (ptv, u))
              .buildColl[Seq]
              .map(pair => ProposalTimeVoteVoteWithUser(pair._1, UserWithoutPassword(pair._2.id, pair._2.name)))

          ProposalTimeWithVotes(
            time,
            votes
          )
        })

      ProposalFull(
        proposal.id,
        proposal.title,
        users
          .filter(_.id === proposal.creatorId)
          .buildColl[Seq]
          .map(u => UserWithoutPassword(u.id, u.name))
          .head,
        ptv
      )
    }))
  }

  def removeProposal(msg: RemoveProposal) = db.withTransaction { implicit session =>
    proposals.filter(_.id === msg.id).delete
    sender ! ProposalRemoved
  }

  def receive = handled {
    case msg: AddProposal          => addProposal(msg)
    case msg: AddProposalTime      => addProposalTime(msg)
    case msg: AddProposalTimeVote  => addProposalTimeVote(msg)
    case msg: GetProposalsFromUser => getProposalsFromUser(msg)
    case msg: RemoveProposal       => removeProposal(msg)
  }
}