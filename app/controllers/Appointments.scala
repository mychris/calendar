package controllers

import akka.pattern.ask

import access.Restricted

import play.api.mvc._

import service._
import service.protocol._

import service.protocol.GetAppointmentById
import service.protocol.GetAppointmentsFromUser
import hirondelle.date4j._


object Appointments
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseTransformation {

  def show(id: Int) = Action.async {
    (Services.calendarService ? GetAppointmentById(id)).mapTo[Response].map(_.toJsonResult)
  }

  def list() = Authenticated.async { implicit request =>
    (Services.calendarService ? GetAppointmentsFromUser(request.user.id)).mapTo[Response].map(_.toJsonResult)
  }

  def add(implicit description: String, start: DateTime, end: DateTime) = Authenticated.async { implicit request =>
    (Services.calendarService ? AddAppointment(description, start, end)).mapTo[Response].map(_.toJsonResult)
  }

  def update(id: Int) = Action {
  	Status(501)("")
  }

  def delete(id: Int) = Action {
  	Status(501)("")
  }
}