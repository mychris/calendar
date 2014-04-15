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
object SampleDataService {

  def props(db: Database): Props = Props(classOf[SampleDataService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  */
class SampleDataService(db: Database)
  extends Actor with
          ActorLogging with
          UserDataAccessComponentImpl with
          CalendarDataAccessComponentImpl with
          AppointmentProposalDataAccessComponentImpl with
          ExceptionHandling {

  protected object userDataAccess extends UserDataAccessModuleImpl
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl

  import userDataAccess._
  import calendarDataAccess._

  def addAppointment(title: String, start: DateTime, end: DateTime, tagId: Int)(implicit session: Session) = {
    val appointmentId = (appointments returning appointments.map(_.id)) += Appointment(-1, title, start, end)
    appointmentBelongsToTag += ((appointmentId, tagId))
    appointmentId
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
        addAppointment("Tim & Caroline visiting", new DateTime("2014-01-17"), new DateTime("2014-01-19"), defaultTagId),
        // tag family
        // first week
        // second week
        addAppointment("Anna piano lesson", new DateTime("2014-01-08 15:00:00"), new DateTime("2014-01-08 16:00:00"), familyTagId),
        addAppointment("Tim soccer"       , new DateTime("2014-01-09 15:00:00"), new DateTime("2014-01-09 17:00:00"), familyTagId),
        addAppointment("Anna theatre     ", new DateTime("2014-01-10 20:00:00"), new DateTime("2014-01-10 22:00:00"), familyTagId),
        // third week
        addAppointment("Anna piano lesson", new DateTime("2014-01-15 15:00:00"), new DateTime("2014-01-15 16:00:00"), familyTagId),
        addAppointment("Tim soccer"       , new DateTime("2014-01-16 15:00:00"), new DateTime("2014-01-16 17:00:00"), familyTagId),
        // fourth week
        addAppointment("Anna piano lesson", new DateTime("2014-01-22 15:00:00"), new DateTime("2014-01-22 16:00:00"), familyTagId),
        addAppointment("Tim soccer"       , new DateTime("2014-01-23 15:00:00"), new DateTime("2014-01-23 17:00:00"), familyTagId),
        // tag work
        addAppointment("Milestone Meeting", new DateTime("2014-01-08 10:00:00"), new DateTime("2014-01-08 12:00:00"), workTagId)
      )
    }

    sender ! SampleDataCreated
  } 

  def receive = handled {
    case CreateSampleData => createSampleData
  }
}