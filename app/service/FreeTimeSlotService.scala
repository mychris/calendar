package service

import akka.actor._

import datasource.calendar._
import datasource.user._

import formatters._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

import util.CustomColumnTypes._
import util.DateTimeExtensions._
import util.JsonConversion._

/**
  *
  * @author Simon Kaltenbacher
  */
object FreeTimeSlotService {

  def props(db: Database): Props = Props(classOf[FreeTimeSlotService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  */
class FreeTimeSlotService(db: Database)
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
    case fftss @ FindFreeTimeSlots(userIds, duration, from, to, startTime, endTime) =>

      log.debug(s"Received free time slot request with ${fftss.toJson}")

      val appointments = db.withSession { implicit session =>
        appointmentsFromUsers(userIds, from, to)
          .buildColl[Seq]
          .filter(a => !(startTime.forall(a.end.timeOfDay < _.timeOfDay) || endTime.forall(_.timeOfDay < a.start.timeOfDay)))
      }

      val constraints: Seq[Appointment] = from.days(to).flatMap { day => 
        Seq(
          startTime.map { startTime =>
            Appointment(-1, "", day.withTimeOfDay(startOfDay), day.withTimeOfDay(startTime.timeOfDay))
          },
          endTime.map { endTime =>
            Appointment(-1, "", day.withTimeOfDay(endTime.timeOfDay), day.withTimeOfDay(endOfDay))
          }
        ).flatten
      }

      val sorted = (appointments ++ constraints :+ Appointment(-1, "", to, to)).sortBy(_.start.millisUTC)

      val freeTimeSlots =
        sorted
          .foldLeft[Acc]((Seq(), from)) {
            case ((slots, lastEnd), appointment) =>
              if(appointment.start.millisUTC - lastEnd.millisUTC >= duration)
                (slots :+ TimeSlot(lastEnd, appointment.start), appointment.end)
              else
                (slots, appointment.end)
          }._1

      log.debug(s"Free time slots ${freeTimeSlots.toJson}")

      sender ! FreeTimeSlots(freeTimeSlots)
  }
}