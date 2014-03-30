package service

import akka.actor._
import akka.routing._

import play.api.db._
import play.api.Play.current
import play.api.libs.concurrent.Akka 

import scala.slick.driver.PostgresDriver.simple._

/** Global service accessor
  *
  * @author Simon Kaltenbacher
  */
object Services {

  val db = Database.forDataSource(DB.getDataSource())

  /** Execution environment */
  val system = Akka.system

  val userService = system.actorOf(UserService.props(db).withRouter(FromConfig()), "user-service")

  val calendarService = system.actorOf(CalendarService.props(db).withRouter(FromConfig()), "calendar-service")
}