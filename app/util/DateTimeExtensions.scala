package util

import hirondelle.date4j.DateTime

import java.util.TimeZone

object DateTimeExtensions {

  /** */
  implicit def dateTime2DateTimeExtensions(dateTime: DateTime) = new DateTimeExtensions(dateTime) 

  /** */
  val timeZoneUTC = TimeZone.getTimeZone("UTC")
}

class DateTimeExtensions(dateTime: DateTime) {

  import DateTimeExtensions._
  
  /** */
  def millisUTC = dateTime.getMilliseconds(timeZoneUTC)

  /** */
  def days(that: DateTime) =
    for(i <- 0 to dateTime.numDaysFrom(that)) yield dateTime.plusDays(i).getStartOfDay

  /** */
  def timeOfDay =
    ((dateTime.getHour * 3600 + dateTime.getMinute * 60 + dateTime.getSecond) * 1000).toLong

  /** */
  def withTimeOf(that: DateTime) =
    that.getStartOfDay.plus(0, 0, 0, that.getHour, that.getMinute, that.getSecond, that.getNanoseconds, DateTime.DayOverflow.Spillover)
}