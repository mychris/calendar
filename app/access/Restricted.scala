package access

import play.api.mvc._
import controllers.routes

trait Restricted {
  private def username(request: RequestHeader) = request.session.get("username")

  private def onUnauthorized(request: RequestHeader) =
    Results.Redirect(routes.LoginController.index).flashing("error" -> "You are not logged in")

  def isLoggedIn(f: String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
}