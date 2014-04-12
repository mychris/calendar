package service.protocol

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

/** */
case class AppointmentWithTags(appointments: Appointment, tags: Seq[Tag])

/*
 * Requests
 */

/* Appointments */
case class GetAppointmentById(id: Int) extends Request
case class GetAppointmentsFromTag(tagId: Int) extends Request
case class GetAppointmentsFromUser(userId: Int) extends Request
case class GetAppointmentsFromUserWithTags(userId: Int) extends Request
case class AddAppointment(title: String, start: DateTime, end: DateTime, tagId: Int) extends Request
case class RemoveAppointments(appointmentIds: Seq[Int]) extends Request

/* Tags */
case class GetTagById(id: Int) extends Request
case class GetTagsFromUser(userId: Int) extends Request
case class GetTagsFromAppointment(appointmentId: Int) extends Request
case class AddTag(name: String, priority: Int, userId: Int) extends Request
case class RemoveTags(tagIds: Seq[Int]) extends Request
case class RemoveTagsFromUser(tagId: Seq[Int], userId: Int) extends Request

/*
 * Reponses
 */

/* Appointments */
case class  AppointmentById(appointment: Appointment) extends Success
case class  AppointmentsFromTag(appointments: Seq[Appointment]) extends Success
case class  AppointmentsFromUser(appointments: Seq[Appointment]) extends Success
case class  AppointmentsFromUserWithTag(appointments: Seq[AppointmentWithTags]) extends Success
case class  AppointmentAdded(id: Int) extends Success
case object AppointmentsRemoved extends Success

/* Tags */
case class TagById(tag: Tag) extends Success
case class TagsFromUser(tags: Seq[Tag]) extends Success
case class TagsFromAppointment(tags: Seq[Tag]) extends Success
case class TagAdded(id: Int) extends Success
case class TagsRemoved() extends Success

/*
 * Errors
 */

case class NoSuchTagError(message: String) extends Error(message)
case class NoSuchAppointmentError(message: String) extends Error(message)