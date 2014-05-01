package controllers

import play.api.mvc._
import com.github.nscala_time.time.Imports._


object Calendar extends Controller with Restricted {

  /**
   * Displays full calendar
   * @return Result
   */
  def calendar = Authenticated { implicit request => {
      val userName = request.session.get("username").getOrElse("n/a")
      Ok(views.html.calendar("Appointment finding Calendar", userName))
    }
  }
}