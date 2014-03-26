package controllers

import play.api._
import play.api.mvc._
import access.Restricted

object Application extends Controller with Restricted {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def hello = isLoggedIn { username => implicit request =>
    Ok(views.html.hello(username))
  }
}