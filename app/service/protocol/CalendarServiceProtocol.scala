package service.protocol

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

/** */
case class AppointmentWithTags(appointments: Appointment, tags: Seq[Tag])

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
case class GetAppointmentsFromTag(tagId: Int) extends Request

/** */
case class GetAppointmentsFromUser(userId: Int) extends Request

/** */
case class GetAppointmentsFromUserWithTags(userId: Int) extends Request

/** */
case class AddTag(name: String, priority: Int, userId: Int) extends Request

/** */
case class AddAppointment(description: String, start: DateTime, end: DateTime, tagId: Int) extends Request

/** */
case class RemoveTags(tagIds: Seq[Int]) extends Request

/** */
case class RemoveAppointments(appointmentIds: Seq[Int]) extends Request

/*
 * Reponses
 */

/** */
case class TagById(tag: Tag) extends Success

/** */
case class AppointmentById(appointment: Appointment) extends Success

/** */
case class TagsFromUser(tags: Seq[Tag]) extends Success

/** */
case class TagsFromAppointment(tags: Seq[Tag]) extends Success

/** */
case class AppointmentsFromTag(appointments: Seq[Appointment]) extends Success

/** */
case class AppointmentsFromUser(appointments: Seq[Appointment]) extends Success

/** */
case class AppointmentsFromUserWithTag(appointments: Seq[AppointmentWithTags]) extends Success

/** */
case class TagAdded(id: Int) extends Success

/** */
case class AppointmentAdded(id: Int) extends Success

/** */
case object TagsRemoved extends Success

/** */
case object AppointmentsRemoved extends Success

/*
 * Errors
 */

/** */
case class NoSuchTagError(message: String) extends Error

/** */
case class NoSuchAppointmentError(message: String) extends Error