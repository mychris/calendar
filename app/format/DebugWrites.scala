package format

import datasource.calendar._
import datasource.user._
import datasource.proposal._

import com.github.nscala_time.time.Imports._

import play.api.libs.json.{util => _, _}

import service.protocol._

import util.JsonConversion._

/**
  *
  * @author Simon Kaltenbacher
  */
object DebugWrites {

  implicit object dateTimeFormat extends Writes[DateTime] {

    def writes(o: DateTime): JsValue = o.toString().toJson
  }

  implicit val addAppointmentWrites = Json.writes[AddAppointment]
  implicit val findFreeTimeSlotsWrites = Json.writes[FindFreeTimeSlots]

  implicit val appointmentWrites = Json.writes[Appointment]
  implicit val timeSlotWrites = Json.writes[TimeSlot]
}