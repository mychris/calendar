package controllers

import akka.pattern.ask

import formatters._

import hirondelle.date4j.DateTime

import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import play.api.Logger
import play.api.templates.Html

import service._
import service.protocol._

import util._


object Application
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling {

  def recoverException: PartialFunction[Throwable, SimpleResult] = {
    case e: Exception => Logger.error(e.getStackTraceString)
                         InternalServerError(e.getMessage)
  }

  def index = Action { Ok(views.html.index("Your new application is ready.")) }

  def admin = Action { Ok(views.html.administration()) }

  /** Create database tables */
  def createSchema = Action.async {
    toJsonResult {
      (Services.administrationService ? CreateSchema).expecting[SchemaCreated.type]
    }
  }

  /** Drop database tables */
  def dropSchema = Action.async {
    toJsonResult {
      (Services.administrationService ? DropSchema).expecting[SchemaDropped.type]
    }
  }

  def createSampleData = Action.async {
    toJsonResult {
      (Services.administrationService ? CreateSampleData).expecting[SampleDataCreated.type]
    }
  }
}