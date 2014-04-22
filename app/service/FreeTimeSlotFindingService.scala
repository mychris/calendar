package service

import akka.actor._

import datasource.calendar._
import datasource.user._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

import util.DateTimeExtensions._

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

  def receive = {
    case FindFreeTimeSlots(duration, from, to, startTime, endTime, userIds) =>

      val appointments = db.withSession { implicit session =>
        appointmentsFromUsers(userIds, from, to)
          .buildColl[Seq]
          .filter(a => startTime.timeOfDay <= a.end.timeOfDay || a.start.timeOfDay <= endTime.timeOfDay)
      }

      val constraints = from.days(to).flatMap { day =>
        Seq(
          Appointment(-1, "", day.withTimeOfDay(startOfDay)       , day.withTimeOfDay(startTime.timeOfDay)),
          Appointment(-1, "", day.withTimeOfDay(endTime.timeOfDay), day.withTimeOfDay(endOfDay           ))
        )
      }

      val sorted = (appointments ++ constraints :+ Appointment(-1, "", to, to)).sortBy(_.start.millisUTC)

      sender ! FreeTimeSlots(
        sorted
          .foldLeft[Acc]((Seq(), from)) {
            case ((slots, lastEnd), appointment) =>
              if(appointment.start.millisUTC - lastEnd.millisUTC >= duration)
                (slots :+ TimeSlot(lastEnd, appointment.start), appointment.end)
              else
                (slots, appointment.end)
          }._1
      )
  }
}