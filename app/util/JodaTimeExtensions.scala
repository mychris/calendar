package util

import com.github.nscala_time.time.Imports._

/**
  *
  * @author Simon Kaltenbacher
  */
object JodaTimeExtensions {

  implicit def dateTime2RichDateTime(dateTime: DateTime): RichDateTime = new RichDateTime(dateTime)
}

class RichDateTime(dateTime: DateTime) {

  def -(that: DateTime): Duration = new Duration(dateTime, that)

  def withTimeAtEndOfDay: DateTime = dateTime.withTime(23, 59, 59, 0)
}