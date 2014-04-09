package controllers

import akka.pattern.ask

import datasource.calendar._

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json.toJson

import scala.concurrent._

import service._
import service.protocol._

import formatters._

case class AddUserRequestBody(name: String, password: String)

object Users
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling with 
          RequestBodyReader {
  
  def add() = Action.async(parse.json) { implicit request =>
    readBody[AddUserRequestBody] { addUser =>
      toJsonResult{(Services.userService ? AddUser(addUser.name, addUser.password)).expecting[UserAdded]}
    }
  }

}