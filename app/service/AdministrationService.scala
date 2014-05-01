package service

import akka.actor._

import datasource.proposal._
import datasource.calendar._
import datasource.user._

import com.github.nscala_time.time.Imports._

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

import util.Color

/**
  *
  * @author Simon Kaltenbacher
  */
object AdministrationService {

  def props(db: Database): Props = Props(classOf[AdministrationService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  */
class AdministrationService(db: Database)
  extends Actor with
          ActorLogging with
          UserDataAccessComponentImpl with
          CalendarDataAccessComponentImpl with
          ProposalDataAccessComponentImpl with
          ExceptionHandling {

  protected object userDataAccess extends UserDataAccessModuleImpl
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl
  protected object proposalDataAccess extends ProposalDataAccessModuleImpl

  import userDataAccess._
  import calendarDataAccess._
  import proposalDataAccess._

  def addAppointment(title: String, start: DateTime, end: DateTime, tagIds: Seq[Int])(implicit session: Session) = {
    val appointmentId = (appointments returning appointments.map(_.id)) += Appointment(-1, title, start, end)
    appointmentBelongsToTag ++= tagIds.map((appointmentId, _))
    appointmentId
  }


  def createSchema = {
    val ddl = userDdl ++ calendarDdl ++ proposalDdl
    db.withTransaction { implicit session =>
      ddl.create
    }

    log.debug("Schema created\n" + ddl.createStatements.mkString("\n"))
    sender ! SchemaCreated
  }

  def dropSchema = {
    val ddl = userDdl ++ calendarDdl ++ proposalDdl
    db.withTransaction { implicit session =>
      ddl.drop
    }

    // println("Schema dropped: " + ddl.dropStatements.mkString("; "))
    log.debug("Schema dropped\n" + ddl.dropStatements.mkString("\n"))
    sender ! SchemaDropped
  }

  def createSampleData = {
    db.withTransaction { implicit session =>

      // Clear tables
      appointments.delete
      tags.delete
      users.delete

      // Insert sample data

      val test      = (users returning users.map(_.id)) += User(-1, "test", "test")
      val simon     = (users returning users.map(_.id)) += User(-1, "Simon", "test")
      val florian   = (users returning users.map(_.id)) += User(-1, "Florian", "test")
      val christoph = (users returning users.map(_.id)) += User(-1, "Christoph", "test")

      val tagIds = (tags returning tags.map(_.id)) ++= Seq(
        Tag(-1, "default", 0, Color.colors(0), test),
        Tag(-1, "family" , 1, Color.colors(1), test),
        Tag(-1, "work"   , 2, Color.colors(2), test),

        Tag(-1, "default", 0, Color.colors(0), simon),
        Tag(-1, "family" , 1, Color.colors(1), simon),
        Tag(-1, "work"   , 2, Color.colors(2), simon),

        Tag(-1, "default", 0, Color.colors(0), florian),
        Tag(-1, "family" , 1, Color.colors(1), florian),
        Tag(-1, "work"   , 2, Color.colors(2), florian),

        Tag(-1, "default", 0, Color.colors(0), christoph),
        Tag(-1, "family" , 1, Color.colors(1), christoph),
        Tag(-1, "work"   , 2, Color.colors(2), christoph)
      )

      val defaultTagId = tagIds(0)
      val familyTagId  = tagIds(1)
      val workTagId    = tagIds(2)

      val appointmentIds = Seq(
        // tag default
        addAppointment("Tim & Caroline visiting", new DateTime("2014-01-17"), new DateTime("2014-01-19"), Seq(defaultTagId)),
        // tag family
        // first week
        // second week
        addAppointment("Anna piano lesson", new DateTime("2014-01-08 15:00:00"), new DateTime("2014-01-08 16:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Tim soccer"       , new DateTime("2014-01-09 15:00:00"), new DateTime("2014-01-09 17:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Anna theatre     ", new DateTime("2014-01-10 20:00:00"), new DateTime("2014-01-10 22:00:00"), Seq(defaultTagId, familyTagId)),
        // third week
        addAppointment("Anna piano lesson", new DateTime("2014-01-15 15:00:00"), new DateTime("2014-01-15 16:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Tim soccer"       , new DateTime("2014-01-16 15:00:00"), new DateTime("2014-01-16 17:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Carl badminton"   , new DateTime("2014-01-16 16:30:00"), new DateTime("2014-01-16 19:00:00"), Seq(defaultTagId, familyTagId)),
        // fourth week
        addAppointment("Anna piano lesson", new DateTime("2014-01-22 15:00:00"), new DateTime("2014-01-22 16:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Tim soccer"       , new DateTime("2014-01-23 15:00:00"), new DateTime("2014-01-23 17:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Release RC1A"     , new DateTime("2014-01-23 09:00:00"), new DateTime("2014-01-23 18:00:00"), Seq(defaultTagId, workTagId)),
        // tag work
        addAppointment("Milestone Meeting", new DateTime("2014-01-08 10:00:00"), new DateTime("2014-01-08 12:00:00"), Seq(defaultTagId, workTagId)),

        // Create 3 appointments for Simon
        addAppointment("Simon appointment #1", new DateTime("2014-01-06 10:00:00"), new DateTime("2014-01-06 15:00:00"), Seq(tagIds(3))),
        addAppointment("Simon appointment #2", new DateTime("2014-01-07 10:00:00"), new DateTime("2014-01-07 15:00:00"), Seq(tagIds(3))),
        addAppointment("Simon appointment #3", new DateTime("2014-01-10 13:00:00"), new DateTime("2014-01-10 14:00:00"), Seq(tagIds(3))),

        // Create 3 appointments for Florian
        addAppointment("Florian appointment #1", new DateTime("2014-01-13 10:00:00"), new DateTime("2014-01-13 15:00:00"), Seq(tagIds(6))),
        addAppointment("Florian appointment #2", new DateTime("2014-01-14 10:00:00"), new DateTime("2014-01-14 15:00:00"), Seq(tagIds(6))),
        addAppointment("Florian appointment #3", new DateTime("2014-01-17 13:00:00"), new DateTime("2014-01-17 14:00:00"), Seq(tagIds(6))),

        // Create 3 appointments for Christoph
        addAppointment("Christoph appointment #1", new DateTime("2014-01-20 10:00:00"), new DateTime("2014-01-20 15:00:00"), Seq(tagIds(9))),
        addAppointment("Christoph appointment #2", new DateTime("2014-01-21 10:00:00"), new DateTime("2014-01-21 15:00:00"), Seq(tagIds(9))),
        addAppointment("Christoph appointment #3", new DateTime("2014-01-24 13:00:00"), new DateTime("2014-01-24 14:00:00"), Seq(tagIds(9)))
      )

      // add proposal with 3 times, test is the creator, other default user are participants.
      val participants = Seq(test, simon, florian, christoph)
      val proposalId = (proposals returning proposals.map(_.id)) += Proposal(-1, "Final presentation", Color.colors(4), test)

      val proposalTime1 = (proposalTimes returning proposalTimes.map(_.id)) += ProposalTime(-1, new DateTime("2014-01-13 15:00"), new DateTime("2014-01-13 17:00"), proposalId)
      proposalTimeVotes ++= participants.map(ProposalTimeVote(proposalTime1, _, Vote.NotVoted))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime1).filter(_.userId === simon)
        .map(v => (v.vote)).update((Vote.Accepted))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime1).filter(_.userId === test)
        .map(v => (v.vote)).update((Vote.Accepted))

      val proposalTime2 = (proposalTimes returning proposalTimes.map(_.id)) += ProposalTime(-1, new DateTime("2014-01-14 15:00"), new DateTime("2014-01-14 17:00"), proposalId)
      proposalTimeVotes ++= participants.map(ProposalTimeVote(proposalTime2, _, Vote.NotVoted))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime2).filter(_.userId === florian)
        .map(v => (v.vote)).update((Vote.Refused))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime2).filter(_.userId === test)
        .map(v => (v.vote)).update((Vote.Accepted))

      val proposalTime3 = (proposalTimes returning proposalTimes.map(_.id)) += ProposalTime(-1, new DateTime("2014-01-15 15:00"), new DateTime("2014-01-15 17:00"), proposalId)
      proposalTimeVotes ++= participants.map(ProposalTimeVote(proposalTime3, _, Vote.NotVoted))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime3).filter(_.userId === christoph)
        .map(v => (v.vote)).update((Vote.Uncertain))
      proposalTimeVotes
        .filter(_.proposalTimeId === proposalTime3).filter(_.userId === test)
        .map(v => (v.vote)).update((Vote.Accepted))
    }

    sender ! SampleDataCreated
  }

  def receive = handled {
    case CreateSchema     => createSchema
    case DropSchema       => dropSchema
    case CreateSampleData => createSampleData
  }
}