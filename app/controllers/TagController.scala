package controllers

import akka.pattern.ask

import access.Restricted

import datasource.calendar._

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json.toJson

import scala.concurrent._

import service._
import service.protocol._

import formatters._

object TagController
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseTransformation {


  def parseJson[T](body: AnyContent)(implicit rds: Reads[T]): Either[Error, T] = {
    body.asJson.flatMap(_.asOpt[T]) match {
      case Some(json) => Right(json)
      case None       => Left(BadFormatError(body.toString))
    }
  }

  def show(id: Int) = Authenticated.async { implicit request =>

    val req = (Services.calendarService ? GetTagById(id)).mapTo[Response]

    for {
      resp <- req
    }
    yield resp.fold[TagById, SimpleResult](
      _.toJsonResult,
      { case tagById @ TagById(tag) if tag.userId == request.user.id => tagById.toJsonResult
        case _                                                       => InternalServerError("Not your tag") }
    )
  }

  def list() = Authenticated.async { implicit request =>
    (Services.calendarService ? GetTagsFromUser(request.user.id)).mapTo[Response].map(_.toJsonResult)
  }

  def add() = Authenticated.async { implicit request =>
    parseJson[Tag](request.body) match {
      case Left(e)      => future { e.toJsonResult }
      case Right(a)     => 
        val req = (Services.calendarService ? AddTag(a.name, a.priority, request.user.id)).mapTo[Response]
        for {
          resp <- req
        }
        yield resp.fold[TagAdded, SimpleResult](
          _.toJsonResult,
          { case tagById @ TagAdded(tag)  => tagById.toJsonResult
            case x                        => x.toJsonResult }
        )
    }
  }

  def update(id: Int) = Action {
    Status(501)("")
  }

  def delete(id: Int) = Action {
    Status(501)("")
  }
}