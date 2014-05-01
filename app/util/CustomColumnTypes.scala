package util

import java.sql.Timestamp
import java.util.TimeZone

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

object CustomColumnTypes {

  implicit def dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  implicit val colorColumnType = MappedColumnType.base[Color, String](
    _.code,
    Color.parse _
  )
}