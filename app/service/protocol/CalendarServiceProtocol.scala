package service.protocol

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

/*
 * Requests
 */

/** */
case class GetTagById(id: Int) extends Request

/** */
case class GetAppointmentById(id: Int) extends Request

/** */
case class GetTagsFromUser(userId: Int) extends Request

/** */
case class GetTagsFromAppointment(appointmentId: Int) extends Request

/** */
case class GetAppointmentsWithTag(tagId: Int) extends Request

/** */
case class AddTag(name: String, priority: Int, userId: Int) extends Request

/** */
case class AddAppointment(description: String, start: DateTime, end: DateTime) extends Request

/** */
case class RemoveTags(tagIds: Seq[Int]) extends Request

/** */
case class RemoveAppointments(appointmentIds: Seq[Int]) extends Request

/*
 * Reponses
 */

/** */
case class TagById(tag: Tag) extends Response

/** */
case class AppointmentById(appointment: Appointment) extends Response

/** */
case class TagsFromUser(tags: Seq[Tag]) extends Response

/** */
case class TagsFromAppointment(tags: Seq[Tag]) extends Response

/** */
case class AppointmentsWithTag(appointments: Seq[Appointment]) extends Response

/** */
case class TagAdded(id: Int) extends Response

/** */
case class AppointmentAdded(id: Int) extends Response

/** */
case object TagsRemoved extends Response

/** */
case object AppointmentsRemoved extends Response

/*
 * Errors
 */

/** */
case class NoSuchTag(message: String) extends Error

/** */
case class NoSuchAppointment(message: String) extends Error