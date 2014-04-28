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
case class FindFreeTimeSlots(userIds: Seq[Int], duration: Int, from: DateTime, to: DateTime, startTime: Option[DateTime], endTime: Option[DateTime]) extends Request

/*
 * Responses
 */

/** */
case class FreeTimeSlots(slots: Seq[TimeSlot]) extends Success