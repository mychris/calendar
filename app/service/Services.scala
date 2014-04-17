package service

import akka.actor._
import akka.pattern.ask
import akka.routing._

import datasource.calendar._

import play.api.db._
import play.api.Logger
import play.api.Play.current

import scala.concurrent.{util => _, _}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.PostgresDriver.SchemaDescription

import service.protocol._

/** Global service accessor
  *
  * @author Simon Kaltenbacher
  */
object Services extends ExecutionEnvironment with ResponseHandling {

  /* Database connection pool */
  val db = Database.forDataSource(DB.getDataSource())

  /** Service for user related operations */
  val userService = system.actorOf(UserService.props(db).withRouter(FromConfig()), "user-service")

  /** Service for basic calendar and tag related operations */
  val calendarService = system.actorOf(CalendarService.props(db).withRouter(FromConfig()), "calendar-service")

  /** Service for basic calendar and tag related operations */
  val conflictFindingService = system.actorOf(ConflictFindingService.props.withRouter(FromConfig()), "conflict-finding-service")

  /** Service for basic calendar and tag related operations */
  val freeTimeSlotsFindingService = system.actorOf(FreeTimeSlotFindingService.props.withRouter(FromConfig()), "freetimeslots-finding-service")

  /** Service for inserting sample data into the database */
  val sampleDataService = system.actorOf(SampleDataService.props(db), "sample-data-service")

  /** Returns a merged ddl statement consisting of all data access component's ddl statements */
  private def collectDdl: Future[SchemaDescription] = {

    val userDdlRequest     = (userService ? GetDdl).expecting[Ddl]
    val calendarDdlRequest = (calendarService ? GetDdl).expecting[Ddl]

    for {
      userDdl     <- userDdlRequest
      calendarDdl <- calendarDdlRequest
    }
    yield userDdl.ddl ++ calendarDdl.ddl
  }

  /** Creates the data access component tables in the database */
  def createSchema: Future[Unit] =
    collectDdl.map { ddl =>
      db.withTransaction { implicit session => ddl.create };
      Logger.debug(ddl.createStatements.mkString("\n"))
    }

  /** Drops the data access component tables in the database */
  def dropSchema: Future[Unit] =
    collectDdl.map { ddl =>
      db.withTransaction { implicit session => ddl.drop };
      Logger.debug(ddl.dropStatements.mkString("\n"))
    }
}