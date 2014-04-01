package access

import play.api.mvc._
import play.api.mvc.Security._

/**
  *
  * @author Florian Liebhart, Simon Kaltenbacher
  */
trait Restricted {

	/** */
  def username(request: RequestHeader) = request.session.get("username")

  // private def onUnauthorized(request: RequestHeader) =
  //   Results.Redirect(routes.LoginController.index).flashing("error" -> "You are not logged in")

  // def isLoggedIn(f: String => Request[AnyContent] => Result) =
  //   Security.Authenticated(username, onUnauthorized) { user =>
  //     Action(request => f(user)(request))
  //   }

  /** */
  object Authenticated extends AuthenticatedBuilder(req => username(req))
}