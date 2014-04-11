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

object Appointments
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling {

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

  def add = Authenticated.async { implicit request =>
    /* val description = request.body.asFormUrlEncoded.get("description")(0)
    val start = request.body.asFormUrlEncoded.get("start")(0)
    val end = request.body.asFormUrlEncoded.get("end")(0)
    val tagId = 1 // Fixme
        val req = (Services.calendarService ? AddAppointment(description, new DateTime(start), new DateTime(end), tagId)).mapTo[Response]
        for {
          resp <- req
        }
        yield resp.fold[AppointmentAdded, SimpleResult](
        _.toJsonResult,
        {
          case appointmentById @ AppointmentAdded(appointment)  => appointmentById.toJsonResult
          case x                                                => x.toJsonResult }
        )*/
    Future.successful(Status(501)(""))
  }

  def update(id: Int) = Action {
  	Status(501)("")
  }

  def delete(id: Int) = Action {
  	Status(501)("")
  }

  def conflicts() = Authenticated.async { implicit request => 
    /*val req = (Services.calendarService ? GetAppointmentsFromUser(request.user.id)).mapTo[Response]
    req.flatMap{
      case AppointmentsFromUser(apps) => {
        ((Services.conflictFindingService ? FindConflict(apps)).mapTo[Response]).map(_.toJsonResult)
      }
      case x => future { x.toJsonResult }
    }*/
    Future.successful(Status(501)(""))
  }

  def freeTimeSlots() = Authenticated.async { implicit request =>
    /* val appsReq = (Services.calendarService ? GetAppointmentsFromUser(request.user.id)).mapTo[Response]

    val duration: Int      = Integer.parseInt(request.body.asFormUrlEncoded.get("duration")(0))*60*1000
    val start   : DateTime = new DateTime(request.body.asFormUrlEncoded.get("start")(0))
    val end     : DateTime = new DateTime(request.body.asFormUrlEncoded.get("end")(0))

    appsReq.flatMap{
      case AppointmentsFromUser(apps) => {
//        val timeSlotsReq = (Services.freeTimeSlotsFindingService ? FindFreeTimeSlots(duration, start, end, apps)).mapTo[Response]
        ((Services.freeTimeSlotsFindingService ? FindFreeTimeSlots(duration, start, end, apps)).mapTo[Response]).map(_.toJsonResult)
//        timeSlotsReq.flatMap{
//          case FreeTimeSlots(slots) => { // slots: Seq[(DateTime, DateTime)]
//            ((Services.calendarService ? FindFreeTimeSlots(apps)).mapTo[Response]).map(_.toJsonResult)
//          }
//          case x => future { x.toJsonResult }
//        }
      }
      case x => future { x.toJsonResult }
    }*/
    Future.successful(Status(501)(""))
  }
}