package util

import hirondelle.date4j.DateTime

import java.util.TimeZone

object DateTimeExtensions {

  /** */
  implicit def dateTime2DateTimeExtensions(dateTime: DateTime) = new DateTimeExtensions(dateTime) 

  /** */
  val timeZoneUTC = TimeZone.getTimeZone("UTC")

  /** */
  val startOfDay = 0

  /** */
  val endOfDay = 24 * 3600 * 1000

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
  def withTimeOfDay(timeOfDay: Long) =
    DateTime.forInstant(dateTime.getStartOfDay.millisUTC + timeOfDay, timeZoneUTC)
}