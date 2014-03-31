package service

import akka.actor._
import akka.pattern.ask
import akka.routing._

import play.api.db._
import play.api.Play.current

import scala.concurrent._
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.PostgresDriver.SchemaDescription

/** Global service accessor
  *
  * @author Simon Kaltenbacher
  */
object Services extends ExecutionEnvironment {

  val db = Database.forDataSource(DB.getDataSource())

  /** */
  val userService = system.actorOf(UserService.props(db).withRouter(FromConfig()), "user-service")

  /** */
  val calendarService = system.actorOf(CalendarService.props(db).withRouter(FromConfig()), "calendar-service")

  /** */
  private def collectDdl: Future[Either[Error, SchemaDescription]] = {

    val userDdlRequest     = (userService ? GetDdl).mapTo[Response]
    val calendarDdlRequest = (calendarService ? GetDdl).mapTo[Response]

    for {
      userDdlResponse     <- userDdlRequest
      calendarDdlResponse <- calendarDdlRequest
    }
    yield {
      for {
        userDdl     <- userDdlResponse.toEither[Ddl].right
        calendarDdl <- calendarDdlResponse.toEither[Ddl].right
      }
      yield userDdl.ddl ++ calendarDdl.ddl
    }
  }

  /** */
  def createSchema: Future[Either[Error, Unit]] =
    collectDdl.map(_.right.map(ddl => db.withTransaction { implicit session => ddl.create }))

  /** */
  def dropSchema: Future[Either[Error, Unit]] =
    collectDdl.map(_.right.map(ddl => db.withTransaction { implicit session => ddl.drop }))
}