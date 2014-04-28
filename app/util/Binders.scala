package util

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.mvc.QueryStringBindable._

object Binders {

  val timeZone = TimeZone.getTimeZone("UTC")

  implicit def stringToSeqInt(listString: String): Seq[Int] =
    listString.split(",").toSeq.map(_.trim.toInt)

  implicit object bindableDateTime extends Parsing[DateTime](
    str => DateTime.forInstant(str.toLong, timeZone),
    _.getMilliseconds(timeZone).toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )
}
