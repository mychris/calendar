package service

import akka.actor._

import datasource.calendar._

import service._

import scala.slick.driver.PostgresDriver.simple._

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

/**
  *
  * @author Christoph Goettschkes
  */
object ConflictFindingService {

  def props: Props = Props(classOf[ConflictFindingService])
}

/**
  *
  * @author Christoph Goettschkes
  */
class ConflictFindingService extends Actor with ActorLogging {

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
    case FindConflict(conflicts) => findConflicts(conflicts)
  }
}