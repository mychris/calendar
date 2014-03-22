package app.util

import hirondelle.date4j.DateTime

import java.sql.Timestamp
import java.util.TimeZone

import scala.slick.driver.PostgresDriver.simple._

package object slick {
  
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    { dateTime  => new Timestamp(dateTime.getNanoseconds.asInstanceOf[Long]) },
    { timestamp => DateTime.forInstantNanos(timestamp.getNanos, TimeZone.getDefault) }
  )
}