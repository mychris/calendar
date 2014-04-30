package controllers

import akka.pattern.ask

import datasource.calendar._

import play.api._
import play.api.mvc._
import play.api.libs.json.{util => _, _}
import play.api.libs.json.Json.toJson

import scala.concurrent.{util => _, _}

import service._
import service.protocol._

import format.ResponseFormat._

import util._

case class AddTagRequestBody(name: String, priority: Int, color: Color)
case class UpdateTagRequestBody(name: String, priority: Int, color: Color)

object Tags
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling with
          RequestBodyReader {


  def show(id: Int) = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.calendarService ? GetTagById(id, request.user.id)).expecting[TagById]
    }
  }

  def list = Authenticated.async { implicit request =>
    toJsonResult { 
      (Services.calendarService ? GetTagsFromUser(request.user.id)).expecting[TagsFromUser]
    }
  }

  def add = Authenticated.async(parse.json) { implicit request =>
    readBody[AddTagRequestBody] { addTag =>
      toJsonResult {
        (Services.calendarService ? AddTag(
          addTag.name,
          addTag.priority,
          addTag.color,
          request.user.id
        )).expecting[TagAdded]
      }
    }
  }

  def update(id: Int) = Authenticated.async(parse.json) { implicit request =>
    readBody[UpdateTagRequestBody] { updateTag =>
      println("UpdateTag")
      toJsonResult {
        (Services.calendarService ? UpdateTag(
          Tag(
            id,
            updateTag.name,
            updateTag.priority,
            updateTag.color,
            request.user.id
          )
        )).expecting[TagUpdated]
      }
    }
  }

  def delete(id: Int) = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.calendarService ? RemoveTagsFromUser(List(id), request.user.id)).expecting[TagsRemoved.type]
    }
  }

  def colors = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.calendarService ? GetColors).expecting[Colors]
    }
  }
}