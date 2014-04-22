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
  val timeZone = TimeZone.getTimeZone("UTC")

  /** */
  def millis(dateTime: DateTime) = dateTime.getMilliseconds(timeZone)

  /** */
  def days(start: DateTime, end: DateTime) =
    for(i <- 0 to start.numDaysFrom(end)) yield start.plusDays(i).getStartOfDay

  /** */
  def timeOfDay(dateTime: DateTime) =
    ((dateTime.getHour * 3600 + dateTime.getMinute * 60 + dateTime.getSecond) * 1000).toLong

  /** */
  val startOfDay = 0

  /** */
  val endOfDay = 24 * 3600 * 1000

  def fromDayWithTimeOfDay(day: DateTime, timeOfDay: Long) =
    DateTime.forInstant(millis(day.getStartOfDay) + timeOfDay, timeZone)

  def receive = {
    case FindFreeTimeSlots(duration, from, to, startTime, endTime, userIds) =>

      val appointments = db.withSession { implicit session =>
        appointmentsFromUsers(userIds, from, to)
          .buildColl[Seq]
          .filter(a => timeOfDay(startTime) <= timeOfDay(a.end) || timeOfDay(a.start) <= timeOfDay(endTime))
      }

      val constraints = days(from, to).flatMap { day =>
        Seq(
          Appointment(-1, "", fromDayWithTimeOfDay(day, startOfDay        ), fromDayWithTimeOfDay(day, timeOfDay(startTime))),
          Appointment(-1, "", fromDayWithTimeOfDay(day, timeOfDay(endTime)), fromDayWithTimeOfDay(day, endOfDay            ))
        )
      }

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