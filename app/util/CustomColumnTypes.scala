package util

import java.sql.Timestamp
import java.util.TimeZone

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

object CustomColumnTypes {
  
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dateTime  => new Timestamp(String.valueOf(dateTime.getNanoseconds).toLong),
    timestamp => DateTime.forInstantNanos(String.valueOf(timestamp.getNanos).toLong, TimeZone.getDefault)
  )

  implicit val colorColumnType = MappedColumnType.base[Color, String](
    _.code,
    Color.parse _
  )
}