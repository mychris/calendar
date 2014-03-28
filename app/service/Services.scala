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

    val db = Database.forDataSource(DB.getDataSource())

    /** Execution environment */
    val system = Akka.system

    /** Data transfer objects */
    object dto {

      /** */
      type User = userDataAccessImpl.User

      /** */
      type Appointment = calendarDataAccessImpl.Appointment

      /** */
      type Tag = calendarDataAccessImpl.Tag

      /** */
      type AppointmentProposal = appointmentProposalDataAccessImpl.AppointmentProposal

      /** */
      type AppointmentProposalTime = appointmentProposalDataAccessImpl.AppointmentProposalTime
    }

    /*
     * Module accessors
     */

    /* */
    object userService extends UserServiceModuleImpl {

      /** User service actors */
      val entryPoint = system.actorOf(userService.factory.userService.withRouter(FromConfig()), "user-service")
    }

    /* */
    object calendarService extends CalendarServiceModuleImpl {

      /** Calendar service actors */
      val entryPoint = system.actorOf(calendarService.factory.calendarService.withRouter(FromConfig()), "calendar-service")
    }

    /* */
    protected val userDataAccessImpl = new UserDataAccessModuleImpl {}

    /* */
    protected val calendarDataAccessImpl = new CalendarDataAccessModuleImpl {}

    /** */
    protected val appointmentProposalDataAccessImpl = new AppointmentProposalDataAccessModuleImpl {}

    /* */
    protected val userDataAccess = userDataAccessImpl

    /* */
    protected val calendarDataAccess = calendarDataAccessImpl
}