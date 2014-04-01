package controllers

import akka.pattern.ask

import access.Restricted

import play.api._
import play.api.mvc._
import play.api.libs.json.Json.toJson

import service._
import service.protocol._

import formatters.AppointmentFormatter._

object AppointmentController extends Controller with Restricted with ExecutionEnvironment {

  def show(id: Int) = Action.async {
  	val req = (Services.calendarService ? GetAppointmentById(id)).mapTo[Response]

    req.map {
      case AppointmentById(appointment)  => Ok(toJson(appointment))
      case DatabaseConnectionError(_)  => Ok("No connection to server!")
    }
  }

  def list() = Action.async { implicit request =>
  	val req = (Services.calendarService ? GetAppointmentsFromUser(request.session.get("userid").map(_.toInt).get)).mapTo[Response]

    req.map {
      case AppointmentsFromUser(appointments)  => Ok(toJson(appointments))
      case DatabaseConnectionError(_)  => Ok("No connection to server!")
    }
  }

  def add() = Action {
  	Status(501)("")
  }

  def update(id: Int) = Action {
  	Status(501)("")
  }

  def delete(id: Int) = Action {
  	Status(501)("")
  }

}