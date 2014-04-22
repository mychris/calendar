package service

import akka.actor._

import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

/**
  *
  * @author Simon Kaltenbacher
  */
object FreeTimeSlotFindingService {

  def props(db: Database): Props = Props(classOf[FreeTimeSlotFindingService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  */
class FreeTimeSlotFindingService(db: Database)
  extends Actor with
          ActorLogging with
          UserDataAccessComponentImpl with
          CalendarDataAccessComponentImpl {

  /** */
  protected object userDataAccess extends UserDataAccessModuleImpl

  /** */
  protected object calendarDataAccess extends CalendarDataAccessModuleImpl

  import calendarDataAccess._

  /** */
  private type Acc = (Seq[TimeSlot], DateTime)

  /** */
  def millis(dateTime: DateTime) = dateTime.getMilliseconds(TimeZone.getTimeZone("UTC"))

  /** */
  def days(start: DateTime, end: DateTime) =
    for(i <- 1 to start.numDaysFrom(end))
      start.plusDays(i)

  def receive = {
    case FindFreeTimeSlots(duration, from, to, startTime, endTime, userIds) =>

      val appointments = db.withSession { implicit session => appointmentsFromUsers(userIds, from, to).buildColl[Seq] }

      sender ! FreeTimeSlots(
        (appointments :+ Appointment(-1, "", to, to))
          .foldLeft[Acc]((Seq(), from)) {
            case ((slots, lastEnd), appointment) =>
              if(millis(appointment.start) - millis(lastEnd) >= duration)
                (slots :+ TimeSlot(lastEnd, appointment.start), appointment.end)
              else
                (slots, appointment.end)
          }._1
      )
  }
}