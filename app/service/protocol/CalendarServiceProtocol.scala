package service.protocol

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

import util._

/** */
case class AppointmentWithTags(appointment: Appointment, tags: Seq[Tag])

/*
 * Requests
 */

/* Appointments */
case class GetAppointmentById(id: Int, userId: Int) extends Request
case class GetAppointmentsFromTag(tagId: Int) extends Request
case class GetAppointmentsFromUser(userId: Int) extends Request
case class GetAppointmentsFromUserWithTags(userId: Int, from: DateTime, to: DateTime) extends Request
case class AddAppointment(title: String, start: DateTime, end: DateTime, tagIds: Seq[Int]) extends Request
case class UpdateAppointmentFromUser(id: Int, title: String, start: DateTime, end: DateTime, tagIds: Seq[Int], userId: Int) extends Request
case class RemoveAppointments(appointmentIds: Seq[Int]) extends Request
case class RemoveAppointmentsFromUser(appointmentIds: Seq[Int], userId: Int) extends Request

/* Tags */
case class GetTagById(id: Int, userId: Int) extends Request
case class GetTagsFromUser(userId: Int) extends Request
case class GetTagsFromAppointment(appointmentId: Int) extends Request
case class AddTag(name: String, priority: Int, color: Color, userId: Int) extends Request
case class UpdateTag(tag: Tag) extends Request
case class RemoveTags(tagIds: Seq[Int]) extends Request
case class RemoveTagsFromUser(tagIds: Seq[Int], userId: Int) extends Request

/* Proposal */
case class AddProposal(title: String, userId: Int) extends Request
case class AddProposalTime(start: DateTime, end: DateTime, proposalId: Int, participants: Seq[Int], userId: Int) extends Request

/*
 * Reponses
 */

/* Appointments */
case class  AppointmentById(appointment: Appointment) extends Success
case class  AppointmentsFromTag(appointments: Seq[Appointment]) extends Success
case class  AppointmentsFromUser(appointments: Seq[Appointment]) extends Success
case class  AppointmentsFromUserWithTags(appointments: Seq[AppointmentWithTags]) extends Success
case class  AppointmentAdded(id: Int) extends Success
case class  AppointmentUpdated(id: Int) extends Success
case object AppointmentsRemoved extends Success

/* Tags */
case class TagById(tag: Tag) extends Success
case class TagsFromUser(tags: Seq[Tag]) extends Success
case class TagsFromAppointment(tags: Seq[Tag]) extends Success
case class TagAdded(id: Int) extends Success
case class TagUpdated(id: Int) extends Success
case object TagsRemoved extends Success

/* Proposal */
case class ProposalAdded(id: Int) extends Success
case class ProposalTimeAdded(id: Int) extends Success

/*
 * Errors
 */

case class NoSuchTagError(message: String) extends Error(message)
case class NoSuchAppointmentError(message: String) extends Error(message)