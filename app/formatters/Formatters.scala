import controllers._

import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util._
import util.JsonConversion._

package object formatters {

  /*
   * Base types
   */

  implicit object dateTimeFormat extends Format[DateTime] {
    def writes(o: DateTime): JsValue = o.getNanosecondsInstant(TimeZone.getDefault).asInstanceOf[Long].toJson

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(ns) => JsSuccess(DateTime.forInstantNanos(ns.toLong, TimeZone.getDefault))
      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
    }
  }

  implicit object colorFormat extends Format[Color] {
    def writes(o: Color): JsValue = o.code.toJson

    def reads(json: JsValue): JsResult[Color] = json match {
      case JsString(Color(color)) => JsSuccess(color)
      case _                      => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }

  /*
   * Generic
   */

  implicit def tupleWrites[A : Writes] = new Writes[(A, A)] {
    def writes(o: (A, A)): JsValue = Seq(o._1, o._2).toJson
  }

  /*
   * UserService
   */

  implicit val userFormat = Json.format[User]

  /* Responses */
  implicit val userByIdFormat = Json.format[UserById]
  implicit val userByNameFormat = Json.format[UserByName]
  implicit val userAddedFormat = Json.format[UserAdded]

  /*
   * CalendarService
   */

  implicit val tagFormat = Json.format[Tag]
  implicit val appointmentFormat = Json.format[Appointment]

  /* Responses */
  implicit val appointmentWithTagsFormat = Json.format[AppointmentWithTags]
  implicit val appointmentByIdFormat = Json.format[AppointmentById]
  implicit val appointmentsFromTagFormat = Json.format[AppointmentsFromTag]
  implicit val appointmentsFromUserFormat = Json.format[AppointmentsFromUser]
  implicit val appointmentsFromUserWithTagFormat = Json.format[AppointmentsFromUserWithTag]
  implicit val appointmentAddedFormat = Json.format[AppointmentAdded]

  implicit val tagByIdFormat = Json.format[TagById]
  implicit val tagsFromUserFormat = Json.format[TagsFromUser]
  implicit val tagsFromAppointmentFormat = Json.format[TagsFromAppointment]
  implicit val tagAddedFormat = Json.format[TagAdded]
  implicit val tagUpdatedFormat = Json.format[TagUpdated]
  implicit object tagsRemoved extends Writes[TagsRemoved] {
    def writes(o: TagsRemoved): JsValue = "".toJson
  }

  /*
   * ConflictFindingService
   */

  /* Responses */
  implicit val conflictsWrites = Json.writes[Conflicts]

  /*
   * FreeTimeSlotFindingService
   */

  implicit val timeSlotFormat = Json.format[TimeSlot]

  /* Responses */
  implicit val freeTimeSlotsFormat = Json.format[FreeTimeSlots]

  /*
   * Exception
   */

  implicit object exceptionWrites extends Writes[Exception] {
    def writes(o: Exception): JsValue = o.getMessage.toJson
  }

  /*
   * Json request bodies
   */

  /* Appointments */
  implicit val addAppointmentRequestBody = Json.format[AddAppointmentRequestBody]

  /* Tags */
  implicit val addTagRequestBody = Json.format[AddTagRequestBody]
  implicit val updateTagRequestBody = Json.format[UpdateTagRequestBody]

  /* Users */
  implicit val addUserRequestBody = Json.format[AddUserRequestBody]
}