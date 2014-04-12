package util

import java.sql.Timestamp
import java.util.TimeZone

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

object CustomColumnTypes {
  
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dateTime  => new Timestamp(dateTime.getNanoseconds.asInstanceOf[Long]),
    timestamp => DateTime.forInstantNanos(timestamp.getNanos, TimeZone.getDefault)
  )

  implicit val colorColumnType = MappedColumnType.base[Color, String](
    _.code,
    Color.parse _
  )
}