package service

import akka.actor._
import akka.pattern.ask
import akka.routing._

import play.api.db._
import play.api.Play.current

import scala.concurrent.{util => _, _}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.PostgresDriver.SchemaDescription

import service.protocol._

import util.exception._

/** Global service accessor
  *
  * @author Simon Kaltenbacher
  */
object Services extends ExecutionEnvironment {

  /* Database connection pool */
  val db = Database.forDataSource(DB.getDataSource())

  /** Service for user related operations */
  val userService = system.actorOf(UserService.props(db).withRouter(FromConfig()), "user-service")

  /** Service for basic calendar and tag related operations */
  val calendarService = system.actorOf(CalendarService.props(db).withRouter(FromConfig()), "calendar-service")

  /** Returns a merged ddl statement consisting of all data access component's ddl statements */
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

  /** Creates the data access component's tables in the database */
  def createSchema: Future[Either[Error, Unit]] =
    collectDdl.map(_.right.flatMap(ddl =>
      exceptionToEither[Exception, Error, Unit](
        db.withTransaction { implicit session => ddl.create },
        e => SchemaChangeError(e.getMessage)
      )
    ))

  /** Drops the data access component's tables in the database */
  def dropSchema: Future[Either[Error, Unit]] =
    collectDdl.map(_.right.flatMap(ddl =>
      exceptionToEither[Exception, Error, Unit](
        db.withTransaction { implicit session => ddl.drop },
        e => SchemaChangeError(e.getMessage)
      )
    ))
}