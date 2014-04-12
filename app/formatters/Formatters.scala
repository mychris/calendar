import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util.json._

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

  /* Requests */
  implicit val getUserByIdFormat = Json.format[GetUserById]
  implicit val getUserByNameFormat = Json.format[GetUserByName]
  implicit val addUserFormat = Json.format[AddUser]

  /* Responses */
  implicit val userByIdFormat = Json.format[UserById]
  implicit val userByNameFormat = Json.format[UserByName]
  implicit val userAddedFormat = Json.format[UserAdded]

  /*
   * CalendarService
   */

  implicit val tagFormat = Json.format[Tag]
  implicit val appointmentFormat = Json.format[Appointment]

  /* Requests */
  implicit val getAppointmentById = Json.format[GetAppointmentById]
  implicit val getAppointmentsFromTag = Json.format[GetAppointmentsFromTag]
  implicit val getAppointmentsFromUser = Json.format[GetAppointmentsFromUser]
  implicit val getAppointmentsFromUserWithTags = Json.format[GetAppointmentsFromUserWithTags]
  implicit val addAppointment = Json.format[AddAppointment]
  implicit val removeAppointments = Json.format[RemoveAppointments]

  implicit val getTagById = Json.format[GetTagById]
  implicit val getTagsFromUser = Json.format[GetTagsFromUser]
  implicit val getTagsFromAppointment = Json.format[GetTagsFromAppointment]
  implicit val addTag = Json.format[AddTag]
  implicit val removeTags = Json.format[RemoveTags]

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

  /*
   * ConflictFindingService
   */

  /* Requests */
  implicit val findConflicts = Json.format[FindConflicts]

  /* Responses */
  implicit val conflictsWrites = Json.writes[Conflicts]

  /*
   * FreeTimeSlotFindingService
   */

  implicit val timeSlotFormat = Json.format[TimeSlot]

  /* Requests */
  implicit val findFreeTimeSlots = Json.format[FindFreeTimeSlots]

  /* Responses */
  implicit val freeTimeSlotsFormat = Json.format[FreeTimeSlots]

  /*
   * Exception
   */

  implicit object exceptionWrites extends Writes[Exception] {

    def writes(o: Exception): JsValue = o.getMessage.toJson 
  }
}