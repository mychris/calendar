package controllers

import akka.pattern.ask

import format.ResponseFormat._

import com.github.nscala_time.time.Imports._

import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import play.api.Logger
import play.api.Routes
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

  /** Javascript router */
  def javascriptRouter = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(

        // Application
        routes.javascript.Application.admin,
        routes.javascript.Application.createSchema,
        routes.javascript.Application.dropSchema,
        routes.javascript.Application.createSampleData,

        // Appointments
        routes.javascript.Appointments.show,
        routes.javascript.Appointments.list,
        routes.javascript.Appointments.add,
        routes.javascript.Appointments.update,
        routes.javascript.Appointments.delete,
        routes.javascript.Appointments.conflicts,

        // Tags
        routes.javascript.Tags.show,
        routes.javascript.Tags.list,
        routes.javascript.Tags.add,
        routes.javascript.Tags.update,
        routes.javascript.Tags.delete,

        // Proposals
        routes.javascript.Proposals.list,
        routes.javascript.Proposals.addWithTimes,
        routes.javascript.Proposals.proposalTimesFromProposal,
        routes.javascript.Proposals.addTime,
        routes.javascript.Proposals.addVote,
        routes.javascript.Proposals.delete,
        routes.javascript.Proposals.findFreeTimeSlots,
        routes.javascript.Proposals.finishVote
      )
    ).as("text/javascript")
  }
}