package util

import hirondelle.date4j.DateTime
import java.util.TimeZone

object Binders {

  /* Attention: The way a DateTime is created string2DateTime and string2OptionDateTime might look weird, but its necessary in order not to have nulls for seconds, minutes,... if e.g. "2014-01-01" is given*/
  implicit def string2DateTime(date: String): DateTime = DateTime.forInstant(new DateTime(date).getMilliseconds(TimeZone.getDefault), TimeZone.getDefault)
  implicit def string2OptionDateTime(date: String): Option[DateTime] = Some(DateTime.forInstant(new DateTime(date).getMilliseconds(TimeZone.getDefault), TimeZone.getDefault))

//  implicit def optionString2DateTime(dateOption: Option[String]): DateTime = dateOption match {
//    case None       => None
//    case Some(date) => Some(new DateTime(date))
//  }

}
