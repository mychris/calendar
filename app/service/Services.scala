package service

import akka.actor._
import akka.routing._

import play.api.db._
import play.api.Play.current
import play.api.libs.concurrent.Akka 

import scala.slick.driver.PostgresDriver.simple._

import datasource._

/** Global service accessor. In terms of the cake pattern [[service.Services]] is the cake.
  *
  * @author Simon Kaltenbacher
  */
object Services extends
  UserServiceComponentImpl with
  CalendarServiceComponentImpl with
  UserDataAccessComponentImpl with
  CalendarDataAccessComponentImpl with
  AppointmentProposalDataAccessComponentImpl {

    /*
     * Configuration
     */

    val db = Database.forDataSource(DB.getDataSource())

    /*
     * Module accessors
     */

    object userService extends UserServiceModuleImpl
    object calendarService extends CalendarServiceModuleImpl
    object userDataAccess extends UserDataAccessModuleImpl
    object calendarDataAccess extends CalendarDataAccessModuleImpl

    val userDataAccessImpl     = userDataAccess
    val calendarDataAccessImpl = calendarDataAccess

    /*
     * Starting services
     */

    // Execution environment
    val system = Akka.system

    // Starting user service
    val user = system.actorOf(userService.factory.userService.withRouter(FromConfig()), "user-service")

    // Starting calendar service
    val calendar = system.actorOf(calendarService.factory.calendarService.withRouter(FromConfig()), "calendar-service")
}