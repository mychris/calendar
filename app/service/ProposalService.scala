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

  def getProposalsForUser(msg: GetProposalsForUser) = db.withSession { implicit session =>
    sender ! ProposalsForUser(
      proposalsForUserWithParticipant(msg.userId)
        .buildColl[Seq]
        .groupBy(_._1)
        .mapValues(_.map(_._2))
        .toSeq
        .map(ProposalWithParticipants.tupled)
    )
  }

  def getProposalTimesFromProposal(msg: GetProposalTimesFromProposal) = db.withSession { implicit session =>
    sender ! ProposalTimesFromProposal(
      proposalTimesWithVotesFromProposal(msg.proposalId)
        .buildColl[Seq]
        .groupBy(_._1)
        .mapValues(_.map { case (_, proposalTimeVote, user) => VoteWithUser(proposalTimeVote.vote, user) })
        .toSeq
        .map(ProposalTimeWithVotes.tupled)
    )
  }

  def removeProposal(msg: RemoveProposal) = db.withTransaction { implicit session =>
    proposals.filter(_.id === msg.id).delete
    sender ! ProposalRemoved
  }

  def receive = handled {
    case msg: AddProposal                  => addProposal(msg)
    case msg: AddProposalTime              => addProposalTime(msg)
    case msg: AddProposalTimeVote          => addProposalTimeVote(msg)
    case msg: GetProposalsForUser          => getProposalsForUser(msg)
    case msg: GetProposalTimesFromProposal => getProposalTimesFromProposal(msg)
    case msg: RemoveProposal               => removeProposal(msg)
  }
}