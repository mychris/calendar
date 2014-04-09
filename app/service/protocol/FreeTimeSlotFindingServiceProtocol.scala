package service.protocol

import datasource.calendar._

import hirondelle.date4j.DateTime

/*
 * Requests
 */

/** */
case class FindFreeTimeSlots(duration: Int, start: DateTime, end: DateTime, appointments: Seq[Appointment])

/*
 * Responses
 */

/** */
case class FreeTimeSlots(slots: Seq[(DateTime, DateTime)])