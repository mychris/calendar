package controllers

import akka.pattern.ask

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.Logger

import scala.concurrent._

import service.Services
import service.Services.dto._
import service.baseprotocol._
import service.Services.userService.protocol._

object LoginController extends Controller with ExecutionEnvironment {

  /** */
  case class LoginData(name: String, password: String)

  /** */
  val userService = Services.userService.entryPoint

  /** */
  def authenticate(loginData: LoginData) = {

    val response = (userService ? GetUserByName(loginData.name)).mapTo[Response]

    response.map {
      case UserByName(user) if user.password == loginData.password => Right(user)
      case UserByName(_) | NoSuchUserError(_)                      => Left("User name or password incorrect!")
      case DatabaseConnectionError(_)                              => Left("No connection to server!")
    }
  }

  /** */
  val form = Form(
    mapping(
      "name"     -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )

  /** */
  def index = Action { implicit request =>
    Ok(views.html.login())
  }

  /** */
  def login = Action.async { implicit request =>

    def error(message: String) = Redirect(routes.LoginController.index).flashing("error" -> message)

    def login(user: User) = Redirect(routes.Application.hello).withSession("username" -> user.name)

    form.bindFromRequest.fold(form => future { error(form.errors.head.message) }, authenticate(_).map(_.fold(error _, login _)))
  }

  /** */
  def logout = Action{ implicit request =>
    Redirect(routes.LoginController.index).withNewSession
  }
}