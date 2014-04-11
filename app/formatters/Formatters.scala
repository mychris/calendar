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

  implicit val appointmentByIdFormat = Json.format[AppointmentById]

  implicit val appointmentsFromTagFormat = Json.format[AppointmentsFromTag]

  implicit val appointmentsFromUserFormat = Json.format[AppointmentsFromUser]

  implicit val appointmentsFromUserWithTagFormat = Json.format[AppointmentsFromUserWithTag]

  implicit val appointmentAddedFormat = Json.format[AppointmentAdded]

  implicit val tagByIdFormat = Json.format[TagById]

  implicit val tagsFromUserFormat = Json.format[TagsFromUser]

  implicit val tagsFromAppointmentFormat = Json.format[TagsFromAppointment]

  implicit val tagAddedFormat = Json.format[TagAdded]

  implicit val userByIdFormat = Json.format[UserById]

  implicit val userByNameFormat = Json.format[UserByName]

  implicit val userAddedFormat = Json.format[UserAdded]

  implicit val conflictsWrites = Json.writes[Conflicts]

  implicit val timeSlotFormat = Json.format[TimeSlot]

  implicit val freeTimeSlotsFormat = Json.format[FreeTimeSlots]

  implicit object exceptionWrites extends Writes[Exception] {

    def writes(o: Exception): JsValue = o.getMessage.toJson 
  }
}