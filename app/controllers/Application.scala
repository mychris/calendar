package controllers

import play.api.mvc._
import akka.pattern.ask

import service._
import service.protocol._

import util._
import play.api.templates.Html

object Application
  extends Controller with
          Restricted with
          ExecutionEnvironment with
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
    yield Ok(views.html.index("User \"test\" created.", Html("<a href=\"/\">Go to Login</a>")))

    result.recover {
      case e: Exception => InternalServerError(e.getMessage)
    }
  }
}