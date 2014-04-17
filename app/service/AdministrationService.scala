package service

import akka.actor._

import datasource.appointmentproposal._
import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

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
          AppointmentProposalDataAccessComponentImpl with
          ExceptionHandling {

  protected object userDataAccess extends UserDataAccessModuleImpl
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl
  protected object appointmentProposalDataAccess extends AppointmentProposalDataAccessModuleImpl

  import userDataAccess._
  import calendarDataAccess._
  import appointmentProposalDataAccess._

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

      val userId = (users returning users.map(_.id)) += User(-1, "test", "test")

      val tagIds = (tags returning tags.map(_.id)) ++= Seq(
        Tag(-1, "default", 0, Color.matrix(1)(1), userId),
        Tag(-1, "family" , 1, Color.matrix(2)(2), userId),
        Tag(-1, "work"   , 2, Color.matrix(3)(3), userId)
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
        // fourth week
        addAppointment("Anna piano lesson", new DateTime("2014-01-22 15:00:00"), new DateTime("2014-01-22 16:00:00"), Seq(defaultTagId, familyTagId)),
        addAppointment("Tim soccer"       , new DateTime("2014-01-23 15:00:00"), new DateTime("2014-01-23 17:00:00"), Seq(defaultTagId, familyTagId)),
        // tag work
        addAppointment("Milestone Meeting", new DateTime("2014-01-08 10:00:00"), new DateTime("2014-01-08 12:00:00"), Seq(defaultTagId, workTagId))
      )
    }

    sender ! SampleDataCreated
  } 

  def receive = handled {
    case CreateSchema     => createSchema
    case DropSchema       => dropSchema
    case CreateSampleData => createSampleData
  }
}