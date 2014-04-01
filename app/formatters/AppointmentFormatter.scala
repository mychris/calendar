package formatters

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json.toJson

import hirondelle.date4j.DateTime
import java.util.TimeZone

import formatters.DateFormatter._

import datasource.calendar.Appointment

object AppointmentFormatter {

  implicit object JsonAppointmentFormatter extends Format[Appointment] {

    def writes(o: Appointment): JsValue = {
      toJson( Map(
        "id" -> toJson(o.id),
        "description" -> toJson(o.description),
        "start" -> toJson(o.start),
        "end" -> toJson(o.end)
      ))
    }

    def reads(json: JsValue): JsResult[Appointment] = {
      JsSuccess(Appointment(
        id = (json \ "id").as[Option[Int]].get,
        description = (json \ "description").as[Option[String]].get,
        start = (json \ "start").as[Option[DateTime]].get,
        end = (json \ "end").as[Option[DateTime]].get
      ))
    }

  }

}
