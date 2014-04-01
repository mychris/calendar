package service

import akka.actor._

import datasource.calendar._

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

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

  def findConflicts(conflicts: Seq[Appointment]) = 
    if (conflicts.size <= 1) {
      sender ! Conflicts(Nil)
    } else {
      val sorted = conflicts.sortBy(a => a.start)
      val result = for (
        first <- sorted;
        second <- sorted.dropWhile(_ != first).drop(1);
        if first != second && second.start.lt(first.end)
      ) yield (first, second)
      sender ! Conflicts(result)
    }

  def receive =  {
    case FindConflict(conflicts) => findConflicts(conflicts)
  }
}