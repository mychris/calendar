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

  def getAppointmentById(msg: GetAppointmentById) = db.withSession { implicit session =>
    appointmentsByIdWithUserId(msg.id).firstOption match {
      case Some((appointment, userId)) if userId == msg.userId => sender !  AppointmentById(appointment)
      case Some(_)                                             => sender !  PermissionDeniedError("Appointment does not belong to specified user!")
      case _                                                   => sender !  NoSuchTagError(s"Appointment with id $msg.id does not exist!")
    }  
  }

  def getAppointmentsFromTag(msg: GetAppointmentsFromTag) = db.withSession { implicit session =>
    sender ! AppointmentsFromTag(appointmentsWithTag(msg.tagId).buildColl[Seq]) }

  def getAppointmentsFromUser(msg: GetAppointmentsFromUser) = db.withSession { implicit session =>
    sender ! AppointmentsFromUser(appointmentsFromUser(msg.userId).buildColl[Seq].distinct)
  }

  /** Retrieves a user appointments together with its tags */
  def getAppointmentsFromUserWithTags(msg: GetAppointmentsFromUserWithTags) = db.withSession { implicit session =>
    sender ! AppointmentsFromUserWithTags(
      appointmentFromUserWithTag(msg.userId, msg.from, msg.to)
        .buildColl[Seq]
        .groupBy(_._1)
        .mapValues(_.map(_._2))
        .toSeq
        .map(AppointmentWithTags.tupled)
    )
  }

  def addAppointment(msg: AddAppointment) =
    sender ! AppointmentAdded(db.withTransaction { implicit session =>
      val appointmentId = (appointments returning appointments.map(_.id)) += Appointment(-1, msg.title, msg.start, msg.end)
      appointmentBelongsToTag ++= msg.tagIds.map((appointmentId, _))
      appointmentId
    })

  def removeAppointments(msg: RemoveAppointments) = db.withSession { implicit session =>
    appointments.filter(_.id.inSet(msg.appointmentIds)).delete
    sender ! AppointmentsRemoved
  }

  def removeAppointmentsFromUser(msg: RemoveAppointmentsFromUser) = db.withSession { implicit session =>
    val tags = tagsFromUser(msg.userId).map(_.id).buildColl[Seq]
    val appointmentsToTags = appointmentBelongsToTag.filter(_.tagId.inSet(tags)).filter(_.appointmentId.inSet(msg.appointmentIds))
    val appointmentsToDelete = appointmentsToTags.map(_.appointmentId).buildColl[Seq].distinct
    appointments.filter(_.id.inSet(appointmentsToDelete)).delete
    appointmentsToTags.delete
    sender ! AppointmentsRemoved
  }

  /*
   * Tags
   */

  def getTagById(msg: GetTagById) = db.withSession { implicit session =>
    tagsById(msg.id).firstOption match {
      case Some(tag) if tag.userId == msg.userId => sender ! TagById(tag)
      case Some(_)                               => sender ! PermissionDeniedError("Tag does not belong to specified user!")
      case _                                     => sender ! NoSuchTagError(s"Tag with id $msg.id does not exist!")
    }
  }

  def getTagsFromUser(msg: GetTagsFromUser) = db.withSession { implicit session =>
    sender ! TagsFromUser(tagsFromUser(msg.userId).buildColl[Seq]) }

  def getTagsFromAppointment(msg: GetTagsFromAppointment) = db.withSession { implicit session =>
    sender ! TagsFromAppointment(tagsFromAppointment(msg.appointmentId).buildColl[Seq]) }

  def addTag(msg: AddTag) = db.withSession { implicit session =>
    sender ! TagAdded((tags returning tags.map(_.id)) += Tag(-1, msg.name, msg.priority, msg.color, msg.userId)) }

  def updateTag(msg: UpdateTag) = db.withSession { implicit session =>
    sender ! TagUpdated(
      tags
        .filter(_.id === msg.tag.id)
        .filter(_.userId === msg.tag.userId)
        .map(t => (t.name, t.priority, t.color))
        .update((msg.tag.name, msg.tag.priority, msg.tag.color))
    )
  }

  def removeTags(msg: RemoveTags) = db.withSession { implicit session =>
    tags.filter(_.id.inSet(msg.tagIds)).delete
    log.debug(s"""Deleting tags with ids ${msg.tagIds.mkString(", ")}""")
    sender ! TagsRemoved
  }

  def removeTagsFromUser(msg: RemoveTagsFromUser) = db.withSession { implicit session =>
    tags.filter(_.id.inSet(msg.tagIds)).filter(_.userId === msg.userId).delete
    log.debug(s"""Deleting tags with ids ${msg.tagIds.mkString(", ")}""")
    sender ! TagsRemoved
  }

  def receive = handled {
    case msg: GetAppointmentById              => getAppointmentById(msg)
    case msg: GetAppointmentsFromUser         => getAppointmentsFromUser(msg)
    case msg: GetAppointmentsFromTag          => getAppointmentsFromTag(msg)
    case msg: GetAppointmentsFromUserWithTags => getAppointmentsFromUserWithTags(msg)
    case msg: GetTagById                      => getTagById(msg)
    case msg: GetTagsFromUser                 => getTagsFromUser(msg)
    case msg: GetTagsFromAppointment          => getTagsFromAppointment(msg)
    case msg: AddTag                          => addTag(msg)
    case msg: UpdateTag                       => updateTag(msg)
    case msg: AddAppointment                  => addAppointment(msg)
    case msg: RemoveTags                      => removeTags(msg)
    case msg: RemoveTagsFromUser              => removeTagsFromUser(msg)
    case msg: RemoveAppointments              => removeAppointments(msg)
    case msg: RemoveAppointmentsFromUser      => removeAppointmentsFromUser(msg)
  }
}