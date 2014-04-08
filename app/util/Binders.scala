package util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Binders {

  implicit def optionString2DateTime(dateOption: Option[String]): Option[DateTime] = dateOption match {
    case None       => None
    case Some(date) => Some(DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd")))
  }
}
