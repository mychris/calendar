package controllers

import access.Restricted

import play.api._
import play.api.mvc._

import service._
import service.protocol._

object AppointmentController extends Controller with Restricted with ExecutionEnvironment {

  def show(id: Int) = Action {
  	Status(501)("")
  }

  def list() = Action {
  	Status(501)("")
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