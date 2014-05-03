package util

import com.github.nscala_time.time.Imports._

import play.api.mvc.QueryStringBindable
import play.api.mvc.QueryStringBindable._

object Binders {

  implicit object bindableDateTime extends Parsing[DateTime](
    str => new DateTime(str.toLong),
    _.getMillis.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )

  implicit object bindableLocalTime extends Parsing[LocalTime](
    str => LocalTime.fromMillisOfDay(str.toLong),
    _.getMillisOfDay.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as LocalTime: %s".format(key, e.getMessage)
  )

  implicit object bindableLocalDate extends Parsing[LocalDate](
    org.joda.time.LocalDate.parse _,
    _.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as LocalDate: %s".format(key, e.getMessage)
  )

  implicit object bindableDuration extends Parsing[Duration](
    str => new Duration(str.toLong),
    _.getMillis.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as Duration: %s".format(key, e.getMessage)
  )

  implicit object bindableDateTimeZone extends Parsing[DateTimeZone](
    DateTimeZone.forID _,
    _.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTimeZone: %s".format(key, e.getMessage)
  )
}
