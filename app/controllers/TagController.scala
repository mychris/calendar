package controllers

import akka.pattern.ask

import access.Restricted

import play.api._
import play.api.mvc._
import play.api.libs.json.Json.toJson

import service._
import service.protocol._

import formatters._

object TagController extends Controller with Restricted with ExecutionEnvironment {

  def show(id: Int) = Action.async { implicit request =>
    // TODO: fix userid
    val userId = request.session.get("userid").map(_.toInt).getOrElse(1)
    val req = (Services.calendarService ? GetTagById(id)).mapTo[Response]

    for {
      resp <- req
    }
    yield resp.fold[TagById, SimpleResult](
      error                                       => InternalServerError("error"),
      { case TagById(tag) if tag.userId == userId => Ok(toJson(tag))
        case TagById(_)                           => InternalServerError("not your tag") }
    )
  }

  def list() = Action.async { implicit request =>
    // TODO: fix userid
    val userId = request.session.get("userid").map(_.toInt).getOrElse(1)
  	val req = (Services.calendarService ? GetTagsFromUser(userId)).mapTo[Response]

    for {
      resp <- req
    }
    yield resp.fold[TagsFromUser, SimpleResult](
      error                     => InternalServerError("error"),
      { case TagsFromUser(tags) => Ok(toJson(tags)) }
    )
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