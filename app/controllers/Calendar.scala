package controllers

import play.api.mvc._
import hirondelle.date4j.DateTime


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

  /**
   * List of events in table view
   * @return Result
   * TODO: Get list of Events from database
   */
  def list(implicit start: Option[DateTime], end: Option[DateTime]) = Action {
    val source = scala.io.Source.fromFile("./public/json/events.json")
    val lines = source.getLines() mkString "\n"
    source.close()

    Ok(lines)
  }
}