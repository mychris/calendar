package controllers

import akka.pattern.ask

import access.Restricted

import play.api._
import play.api.mvc._
import play.api.libs.json.Json.toJson

import service._
import service.protocol._

import formatters.TagFormatter._

object TagController extends Controller with Restricted with ExecutionEnvironment {

  def show(id: Int) = Action.async {
  	val req = (Services.calendarService ? GetTagById(id)).mapTo[Response]

    req.map {
      case TagById(tag)  => Ok(toJson(tag))
      case DatabaseConnectionError(_)  => Ok("No connection to server!")
    }
  }

  def list() = Action.async { implicit request =>
  	val req = (Services.calendarService ? GetTagsFromUser(request.session.get("userid").map(_.toInt).get)).mapTo[Response]

    req.map {
      case TagsFromUser(tags)  => Ok(toJson(tags))
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