package service.protocol

import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

/** */
case class TimeSlot(start: DateTime, end: DateTime)

/*
 * Requests
 */

/** */
case class FindFreeTimeSlots(duration: Int, from: DateTime, to: DateTime, startTime: DateTime, endTime: DateTime, userIds: Seq[Int]) extends Request

/*
 * Responses
 */

/** */
case class FreeTimeSlots(slots: Seq[TimeSlot]) extends Success