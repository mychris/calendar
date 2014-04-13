package util

import java.sql.Timestamp
import java.util.TimeZone

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

object CustomColumnTypes {

  val bla = dateColumnType
  
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dateTime  => new Timestamp(dateTime.getMilliseconds(TimeZone.getTimeZone("UTC")).toLong),
    timestamp => DateTime.forInstant(timestamp.getTime.toLong, TimeZone.getTimeZone("UTC"))
  )

  implicit val colorColumnType = MappedColumnType.base[Color, String](
    _.code,
    Color.parse _
  )
}