package controllers

import akka.pattern.ask
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import play.api.templates.Html
import scala.concurrent.Future

import hirondelle.date4j.DateTime

import service._
import service.protocol._
import util._
import formatters._

object Application
  extends Controller with
          Restricted with
          ExecutionEnvironment with
          ResponseSerialization with
          ResponseHandling {

  def index = Action { Ok(views.html.index("Your new application is ready.")) }

  /** Create database tables */
  def createSchema = Action.async {
    Services.createSchema
      .map(_ => Ok(views.html.index("Database tables have been created!", Html("<a href=\"/createuser\">Create test user</a>"))))
      .recover {
        case e: Exception => InternalServerError(e.getMessage)
      }
  }

  /** Drop database tables */
  def dropSchema = Action.async {
    Services.dropSchema
      .map(_ => Ok(views.html.index("Database tables have been dropped!", Html("<a href=\"/createschema\">Create Schema</a>"))))
      .recover {
        case e: Exception => InternalServerError(e.getMessage)
      }
  }

  /** Create test user with default tag */
  def createUser = Action.async {

    val result = for {
      userAdded <- (Services.userService ? AddUser("test", "test")).expecting[UserAdded]
      tagAdded  <- (Services.calendarService ? AddTag("default", 0, Color.parse("#000000"), userAdded.id)).expecting[TagAdded]
    }
    yield Ok(views.html.index("User \"test\" created.", Html("<a href=\"/createsampledata\">Create sample data</a><br /><a href=\"/\">Go to Login</a>")))

    result.recover {
      case e: Exception => InternalServerError(e.getMessage)
    }
  }

  def createSampleData() = Action.async {

    val source = scala.io.Source.fromFile("./public/json/sampleevents.json")
    val lines = source.getLines() mkString "\n"
    source.close()
    val json: JsValue = Json.parse(lines)
    val events = json.as[Seq[Map[String, String]]]

    val futuresAppointmentAdded = events.map {
      event =>
        toJsonResult {
          (Services.calendarService ? AddAppointment(
            event.get("title").get,
            new DateTime(event.get("start").get),
            new DateTime(event.get("end").get),
            event.get("tagId").get.toInt
          )).expecting[AppointmentAdded]
        }
        .map( result =>
          Html(result.header.status + ": " + event.get("title").get)
        )
    }

    val futureResults = Future.sequence(futuresAppointmentAdded)

    futureResults.map( results =>
      Ok(views.html.index(
        "Sample data created.",
        Html("<a href=\"/\">Go to Login</a>"),
        results
      ))
    )
  }
}