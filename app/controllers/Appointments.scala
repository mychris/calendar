package controllers

import play.api.mvc._
import play.api.mvc.SimpleResult
import play.api.libs.json.Reads

import akka.pattern.ask
import scala.concurrent._

import hirondelle.date4j.DateTime

import formatters._

import service._
import service.protocol._
import java.util.TimeZone

case class AddAppointmentRequestBody(title: String, start: DateTime, end: DateTime, tagId: Int)

object Appointments
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling with
          RequestBodyReader {

  def show(id: Int) = Action.async {
    toJsonResult {
      (Services.calendarService ? GetAppointmentById(id)).expecting[AppointmentById]
    }
  }

  /** case: "from" is not given: all appointments within left opened time interval are being received (all since 1970, until "to"),
    * case: "to" respectively.
    * case: both are not given: All appointments are being received.
    */
  def list(from: Option[DateTime], to: Option[DateTime]) = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.calendarService ? GetAppointmentsFromUserWithTags(
        request.user.id,
        from.getOrElse(DateTime.forInstant(0, TimeZone.getTimeZone("UTC"))),
        to.getOrElse(DateTime.now(TimeZone.getTimeZone("UTC"))
        ))).expecting[AppointmentsFromUserWithTag]
    }
  }

  def add = Authenticated.async(parse.json) { implicit request =>
    readBody[AddAppointmentRequestBody] { addAppointment =>
      toJsonResult {
        (Services.calendarService ? AddAppointment(
          addAppointment.title,
          addAppointment.start,
          addAppointment.end,
          addAppointment.tagId
        )).expecting[AppointmentAdded]
      }
    }
  }

  def update(id: Int) = Action {
    Status(501)("Updating appointments not implemented")
  }

  def delete(id: Int) = Action {
    Status(501)("Deleting appointments not implemented")
  }

  def conflicts = Authenticated.async { implicit request => 
    toJsonResult {
      for {
        AppointmentsFromUser(appointments) <- (Services.calendarService ? GetAppointmentsFromUser(request.user.id)).expecting[AppointmentsFromUser]
        conflicts                          <- (Services.conflictFindingService ? FindConflicts(appointments)).expecting[Conflicts]
      }
      yield conflicts
    }
  }

  def freeTimeSlots() = Authenticated.async { implicit request =>
    Future.successful(Status(501)(""))
  }
}