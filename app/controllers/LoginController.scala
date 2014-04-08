package controllers

import akka.pattern.ask

import datasource.user._

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.Logger

import scala.concurrent._

import service._
import service.protocol._

/** */
case class LoginData(name: String, password: String)

object LoginController extends Controller with ExecutionEnvironment {

  /** */
  def error(message: String) = Redirect(routes.LoginController.index).flashing("error" -> message)

  /** */
  def login(user: User) = Redirect(routes.Calendar.calendar).withSession("username" -> user.name, "userid" -> user.id.toString)

  /** */
  def authenticate(loginData: LoginData): Future[SimpleResult] = {

    val request = (Services.userService ? GetUserByName(loginData.name)).mapTo[Response]

    request.map {
      case UserByName(user) if user.password == loginData.password => login(user)
      case UserByName(_) | NoSuchUserError(_)                      => error("User name or password incorrect!")
      case DatabaseConnectionError(_)                              => error("No connection to server!")
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
  def index = Action { implicit request => Ok(views.html.login(form)) }

  /** */
  def login = Action.async { implicit request =>
    form.bindFromRequest.fold(
      form => future { error(form.errors.head.message) },
      authenticate _
    )
  }

  /** */
  def logout = Action{ implicit request => Redirect(routes.LoginController.index).withNewSession }
}