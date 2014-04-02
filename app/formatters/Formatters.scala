import datasource.calendar._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.json.Json._

package object formatters {

  /** */
  implicit val tagFormat = Json.format[Tag]

  /** */
  implicit val appointmentFormat = Json.format[Appointment]

  /** */
  implicit object dateTimeFormat extends Format[DateTime] {

    def writes(o: DateTime): JsValue = {
      toJson(o.getNanoseconds.asInstanceOf[Long])
    }

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(ns) => JsSuccess(DateTime.forInstantNanos(ns.toLong, TimeZone.getDefault))
      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
    }
  }
}