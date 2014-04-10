import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util.json._

package object formatters {

  implicit val userFormat = Json.format[User]
  implicit val tagFormat = Json.format[Tag]

  implicit object dateTimeFormat extends Format[DateTime] {

    def writes(o: DateTime): JsValue = o.getNanosecondsInstant(TimeZone.getDefault).asInstanceOf[Long].toJson

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(ns) => JsSuccess(DateTime.forInstantNanos(ns.toLong, TimeZone.getDefault))
      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
    }
  }

  implicit val appointmentFormat = Json.format[Appointment]
  implicit val appointmentWithTagsFormat = Json.format[AppointmentWithTags]

  implicit def tupleWrites[A : Writes] = new Writes[(A, A)] {

    def writes(o: (A, A)): JsValue = Seq(o._1, o._2).toJson
  }

  implicit object successWrites extends Writes[Success] {

    def writes(o: Success): JsValue = o match {
      case AppointmentById(appointment)                      => appointment.toJson
      case AppointmentsFromTag(appointments)                 => appointments.toJson
      case AppointmentsFromUser(appointments)                => appointments.toJson
      case AppointmentsFromUserWithTag(appointmentsWithTags) => appointmentsWithTags.toJson
      case AppointmentAdded(id)                              => id.toJson
      case AppointmentsRemoved                               => "".toJson
      case TagById(tag)                                      => tag.toJson
      case TagsFromUser(tags)                                => tags.toJson
      case TagsFromAppointment(tags)                         => tags.toJson
      case TagAdded(id)                                      => id.toJson
      case TagsRemoved                                       => "".toJson
      case UserById(user)                                    => user.toJson
      case UserByName(user)                                  => user.toJson
      case UserAdded(id)                                     => id.toJson
      case Conflicts(conflicts)                              => conflicts.toJson
      case FreeTimeSlots(slots)                              => slots.toJson
      case _                                                 => throw new Exception("Unkown response type!")
    }
  }

  implicit object errorWrites extends Writes[Error] {

    def writes(o: Error): JsValue = o.message.toJson 
  }
}