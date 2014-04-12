package controllers

import play.api.mvc._
import play.api.mvc.SimpleResult
import play.api.libs.json.Reads

import akka.pattern.ask
import scala.concurrent._

import hirondelle.date4j._

import formatters._

import service._
import service.protocol._

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

  def list = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.calendarService ? GetAppointmentsFromUser(request.user.id)).expecting[AppointmentsFromUser]
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
    Status(501)("")
  }

  def delete(id: Int) = Action {
    Status(501)("")
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