package service

import akka.actor._

import datasource._

import service.baseprotocol._

import scala.slick.driver.PostgresDriver.simple._

/**
 *
 * @author Christoph Goettschkes
 */
trait ConflictFindingServiceComponent {

  self: UserDataAccessComponentImpl with
        CalendarDataAccessComponentImpl =>

  /** Database */
  val db: Database

  /** User data access module accessor */
  protected val userDataAccessImpl: UserDataAccessModuleImpl

  /** User data access module accessor */
  protected val calendarDataAccessImpl: CalendarDataAccessModuleImpl

  import userDataAccessImpl._
  import calendarDataAccessImpl._
  
  trait ConflictFindingServiceModule {

    /** */
  	object protocol {

  	  /*
  	   * Requests
  	   */

  	  /** */
      case class FindConflict(appointments: List[Appointment]) extends Request

      /*
       * Reponses
       */

      /** */
      case class Conflicts(conflicts: List[(Appointment, Appointment)]) extends Response

  	}

  	/** */
    trait PropsFactory {

      def conflictFindingService: Props
    }

    /** */
    val factory: PropsFactory

    /** */
    trait ConflictFindingService extends Actor

  }

}

/**
 *
 * @author Christoph Goettschkes
 */
trait ConflictFindingServiceComponentImpl extends ConflictFindingServiceComponent {

  self: UserDataAccessComponentImpl with 
        CalendarDataAccessComponentImpl =>

  import userDataAccessImpl._
  import calendarDataAccessImpl._

  trait ConflictFindingServiceModuleImpl extends ConflictFindingServiceModule {

    import protocol._

    /** */
    object factory extends PropsFactory {

      def conflictFindingService = Props(classOf[ConflictFindingServiceModuleImpl])
    }

    /** */
    class ConflictFindingServiceImpl extends ConflictFindingService with Actor with ActorLogging {

      def findConflicts(conflicts: List[Appointment]) {
      	val sorted = conflicts.sortBy(a => a.start)
        val result = sorted.
          zip(sorted.drop(1)).
          filter({ pair =>
            pair._2.start.lt(pair._1.end)
          })
        sender ! Conflicts(result)
      }

      def receive =  {
        case FindConflict(conflicts)     => findConflicts(conflicts)
      }
      
    }
  }
}