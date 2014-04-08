package controllers

import play.api.mvc._
import hirondelle.date4j.DateTime
import access.Restricted


object Calendar extends Controller with Restricted {


  /**
   * Displays full calendar
   * @return Result
   */
  def calendar = Authenticated { implicit request => {
      val userName = request.session.get("username").getOrElse("unknown user")
      Ok(views.html.calendar("Title of this calendar...", userName))
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