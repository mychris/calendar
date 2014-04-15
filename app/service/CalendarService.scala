package service

import akka.actor._

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag =>_, _}

import service.protocol._

import util._

/**
 *
 * @author Simon Kaltenbacher
 */
object CalendarService {

  def props(db: Database): Props = Props(classOf[CalendarService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  * @author Florian Liebhart
  */
class CalendarService(db: Database)
  extends Actor with 
          ActorLogging with
          UserDataAccessComponentImpl with
          CalendarDataAccessComponentImpl with
          ExceptionHandling {

  /** */
  protected object userDataAccess extends UserDataAccessModuleImpl

  /** */
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl

  import calendarDataAccess._

  /*
   * Appointments
   */

  def getAppointmentById(id: Int, userId: Int) = db.withSession { implicit session =>
    appointmentsByIdWithUserId(id).firstOption match {
      case Some((appointment, `userId`)) => sender !  AppointmentById(appointment)
      case Some(_)                       => sender !  PermissionDeniedError("Appointment does not belong to specified user!")
      case _                             => sender !  NoSuchTagError(s"Appointment with id $id does not exist!")
    }  
  }

  def getAppointmentsFromTag(tagId: Int) = db.withSession { implicit session =>
    sender ! AppointmentsFromTag(appointmentsWithTag(tagId).buildColl[Seq]) }

  def getAppointmentsFromUser(userId: Int) = db.withSession { implicit session =>
    sender ! AppointmentsFromUser(appointmentsFromUser(userId).buildColl[Seq])
  }

  /** Retrieves a user appointments together with its tags */
  def getAppointmentsFromUserWithTags(userId: Int, from: DateTime, to: DateTime) = db.withSession { implicit session =>
    sender ! AppointmentsFromUserWithTag(
      appointmentFromUserWithTag(userId, from, to)
        .buildColl[Seq]
        .groupBy(_._1)
        .mapValues(_.map(_._2))
        .toSeq
        .map(AppointmentWithTags.tupled)
    )
  }

  def getAppointmentsFromUser(userId: Int, from: DateTime, to: DateTime) = db.withSession { implicit session =>
    sender ! AppointmentsFromUser(appointmentsFromUser(userId).buildColl[Seq])
  }

  def addAppointment(title: String, start: DateTime, end: DateTime, tagId: Int) =
    sender ! AppointmentAdded(db.withTransaction { implicit session =>
      val appointmentId = (appointments returning appointments.map(_.id)) += Appointment(-1, title, start, end)
      appointmentBelongsToTag += ((appointmentId, tagId))
      appointmentId
    })

  def removeAppointments(appointmentIds: Seq[Int]) = db.withSession { implicit session =>
    appointments.filter(_.id.inSet(appointmentIds)).delete
    sender ! AppointmentsRemoved
  }

  /*
   * Tags
   */

  def getTagById(id: Int, userId: Int) = db.withSession { implicit session =>
    tagsById(id).firstOption match {
      case Some(tag @ Tag(_, _, _, _, `userId`)) => sender ! TagById(tag)
      case Some(_)                               => sender ! PermissionDeniedError("Tag does not belong to specified user!")
      case _                                     => sender ! NoSuchTagError(s"Tag with id $id does not exist!")
    }
  }

  def getTagsFromUser(userId: Int) = db.withSession { implicit session =>
    sender ! TagsFromUser(tagsFromUser(userId).buildColl[Seq]) }

  def getTagsFromAppointment(appointmentId: Int) = db.withSession { implicit session =>
    sender ! TagsFromAppointment(tagsFromAppointment(appointmentId).buildColl[Seq]) }

  def addTag(name: String, priority: Int, color: Color, userId: Int) = db.withSession { implicit session =>
    sender ! TagAdded((tags returning tags.map(_.id)) += Tag(-1, name, priority, color, userId)) }

  def updateTag(newTag: Tag) = db.withSession { implicit session =>
    sender ! TagUpdated(
      tags
        .filter(_.id === newTag.id)
        .filter(_.userId === newTag.userId)
        .map(t => (t.name, t.priority))
        .update((newTag.name, newTag.priority))
    )
  }

  def removeTags(tagIds: Seq[Int]) = db.withSession { implicit session =>
    tags.filter(_.id.inSet(tagIds)).delete
    sender ! TagsRemoved
  }

  def removeTags(tagIds: Seq[Int], userId: Int) = db.withSession { implicit session =>
    tags.filter(_.id.inSet(tagIds)).filter(_.userId === userId).delete
    sender ! TagsRemoved
  }

  def getDdl = sender ! Ddl(calendarDdl)

  def receive = handled {
    case GetAppointmentById(id, userId)                    => getAppointmentById(id, userId)
    case GetAppointmentsFromUser(id)                       => getAppointmentsFromUser(id)
    case GetAppointmentsFromTag(tagId)                     => getAppointmentsFromTag(tagId)
    case GetAppointmentsFromUserWithTags(userId, from, to) => getAppointmentsFromUserWithTags(userId, from, to)
    case GetTagById(id, userId)                            => getTagById(id, userId)
    case GetTagsFromUser(userId)                           => getTagsFromUser(userId)
    case GetTagsFromAppointment(appointmentId)             => getTagsFromAppointment(appointmentId)
    case AddTag(name, priority, color, userId)             => addTag(name, priority, color, userId)
    case UpdateTag(newTag)                                 => updateTag(newTag)
    case AddAppointment(title, start, end, tagId)          => addAppointment(title, start, end, tagId)
    case RemoveTags(tagIds)                                => removeTags(tagIds)
    case RemoveTagsFromUser(tagIds, userId)                => removeTags(tagIds, userId)
    case RemoveAppointments(appointmentIds)                => removeAppointments(appointmentIds)
    case GetDdl                                            => getDdl
  }
}