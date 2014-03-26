package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.Logger
import models.User

object LoginController extends Controller {
  // überprüfung der formulareingabe
  val input = tuple(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText
  ) verifying("wrong username or password", (input => User.exists(input._1, input._2)))

  val form = Form(input)

  def index = Action { implicit request =>
    Ok(views.html.login())
  }

  def doLogin = Action { implicit request =>
    Logger.debug("doing login")
    form.bindFromRequest().fold (
      formWithErrors => Redirect(routes.LoginController.index).flashing("error" -> "Invalid input"),
      user => Redirect(routes.Application.hello).withSession("username" -> user._1)
    )
  }

  def doLogout = Action{ implicit request =>
    Redirect(routes.LoginController.index).withNewSession
  }

}