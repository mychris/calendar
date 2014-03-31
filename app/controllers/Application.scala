package controllers

import access.Restricted

import play.api._
import play.api.mvc._

import service._

object Application extends Controller with Restricted with ExecutionEnvironment {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  /** */
  def createSchema = Action.async {
    Services.createSchema.map(_.fold(
      _ => Results.InternalServerError,
      _ => Ok("Database tables have been created!")
    ))
  }

  /** */
  def dropSchema = Action.async {
    Services.dropSchema.map(_.fold(
      _ => Results.InternalServerError,
      _ => Ok("Database tables have been dropped!")
    ))
  }

  def hello = isLoggedIn { username => implicit request =>
    Ok(views.html.hello(username))
  }
}