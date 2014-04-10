package util

import scala.slick.driver.PostgresDriver.simple._

import java.sql.Timestamp
import java.util.TimeZone

import hirondelle.date4j.DateTime


package object slick {
  
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    { dateTime  => new Timestamp(dateTime.getNanoseconds.asInstanceOf[Long]) },
    { timestamp => DateTime.forInstantNanos(timestamp.getNanos, TimeZone.getDefault) }
  )
}