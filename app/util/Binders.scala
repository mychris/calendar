package util

import hirondelle.date4j.DateTime
import java.util.TimeZone

object Binders {

  implicit def string2DateTime(date: String): DateTime = new DateTime(date)
  implicit def string2OptionDateTime(date: String): Option[DateTime] = Some(new DateTime(date))


  implicit def optionString2OptionDateTime(dateOption: Option[String]): Option[DateTime] = dateOption match {
    case None       => None
    case Some(date) => Some(new DateTime(date))
  }

  implicit def optionLong2OptionDateTime(millisLongOption: Option[Long]): Option[DateTime] = millisLongOption match {
    case None       => None
    case Some(date) => Some(DateTime.forInstant(date, TimeZone.getTimeZone("UTC")))
  }
}
