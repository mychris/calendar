import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util.json._

package object formatters {

  /** */
  implicit val userFormat = Json.format[User]

  /** */
  implicit val tagFormat = Json.format[Tag]

  /** */
  implicit val appointmentFormat = Json.format[Appointment]

  /** */
  implicit object dateTimeFormat extends Format[DateTime] {

    def writes(o: DateTime): JsValue = o.getNanoseconds.asInstanceOf[Long].toJson

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(ns) => JsSuccess(DateTime.forInstantNanos(ns.toLong, TimeZone.getDefault))
      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
    }
  }

  /** */
  implicit object successWrites extends Writes[Success] {

    def writes(o: Success): JsValue = o match {
      case UserById(user)                     => user.toJson
      case UserByName(user)                   => user.toJson
      case UserAdded(id)                      => id.toJson
      case TagById(tag)                       => tag.toJson
      case AppointmentById(appointment)       => appointment.toJson
      case TagsFromUser(tags)                 => tags.toJson
      case TagsFromAppointment(tags)          => tags.toJson
      case AppointmentsWithTag(appointments)  => appointments.toJson
      case AppointmentsFromUser(appointments) => appointments.toJson
      case TagAdded(id)                       => id.toJson
      case AppointmentAdded(id)               => id.toJson
      case TagsRemoved                        => "".toJson
      case AppointmentsRemoved                => "".toJson
      case _                                  => throw new Exception("Unkown response type!")
    }
  }

  /** */
  implicit object errorWrites extends Writes[Error] {

    def writes(o: Error): JsValue = o.message.toJson 
  }
}