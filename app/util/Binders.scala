package util

import hirondelle.date4j.DateTime

object Binders {

  // TODO: How to pass date formatter in date4j?  (Needed is: "yyyy-MM-dd"!)
  implicit def string2DateTime(date: String): DateTime = new DateTime(date)

  implicit def optionString2DateTime(dateOption: Option[String]): Option[DateTime] = dateOption match {
    case None       => None
    case Some(date) => Some(new DateTime(date))   // TODO: How to pass date formatter in date4j?  (Needed is: "yyyy-MM-dd"!)
  }
}
