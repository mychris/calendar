package service.protocol

import datasource.calendar._

import hirondelle.date4j.DateTime

/** */
case class TimeSlot(start: DateTime, end: DateTime)

/*
 * Requests
 */

/** */
case class FindFreeTimeSlots(duration: Int, start: DateTime, end: DateTime, appointments: Seq[Appointment]) extends Request

/*
 * Responses
 */

/** */
case class FreeTimeSlots(slots: Seq[TimeSlot]) extends Success