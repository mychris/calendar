package controllers

import play.api.mvc._
import akka.pattern.ask

import access.Restricted
import service._
import service.protocol._

object Application extends Controller with Restricted with ExecutionEnvironment {

  def index = Action {

    Ok(views.html.index("Your new application is ready."))
  }

  /** */
  def createSchema = Action.async {
    Services.createSchema.map(_.fold(
      { case Error(message) => InternalServerError(message) },
      _                     => Ok("Database tables have been created!")
    ))
  }

  /** */
  def createDefaultUser = Action.async {

    for {
      (Services.userService ? AddUser("test", "test")).mapTo[Response].map(_.toEither[UserAdded])
    }
    yield {
      for {
        userAdded <- addUserResponse.toEither[UserAdded].right
      }
      yield 
    }

    tagAdded  <- addTagResponse.toEither[TagAdded].right
    addTagResponse  <- (Services.calendarService ? AddTag("default", 0, userId)).mapTo[Response]
    request.map {
      case UserAdded(_) => Ok(views.html.index("User 'test' created."))
      case _            => InternalServerError(views.html.index("Error creating User 'test'"))
    }
  }

  /** */
  def dropSchema = Action.async {
    Services.dropSchema.map(_.fold(
      { case Error(message) => InternalServerError(message) },
      _                     => Ok("Database tables have been dropped!")
    ))
  }
}