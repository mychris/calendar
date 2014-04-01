package formatters

import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.Format

import hirondelle.date4j.DateTime
import java.util.TimeZone

object DateFormatter {

  implicit object JsonDateTimeFormatter extends Format[DateTime] {

    def writes(o: DateTime): JsValue = {
      toJson(o.getNanoseconds.asInstanceOf[Long])
    }

    def reads(json: JsValue): JsResult[DateTime] = {
      JsSuccess(json.as[Option[Long]].map(timestamp => DateTime.forInstantNanos(timestamp, TimeZone.getDefault)).get)
    }
  }

}