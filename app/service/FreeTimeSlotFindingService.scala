package service

import akka.actor._

import datasource.calendar._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

/**
  *
  * @author Simon Kaltenbacher
  */
object FreeTimeSlotFindingService {

  def props: Props = Props[FreeTimeSlotFindingService]
}

/**
  *
  * @author Simon Kaltenbacher
  */
class FreeTimeSlotFindingService extends Actor with ActorLogging {

  /** */
  private type Acc = (Seq[TimeSlot], DateTime)

  /** */
  def millis(dateTime: DateTime) = dateTime.getMilliseconds(TimeZone.getTimeZone("UTC"))

  def receive = {
    case FindFreeTimeSlots(duration, start, end, appointments) =>

      sender ! FreeTimeSlots(
        (appointments :+ Appointment(-1, "", end, end))
          .foldLeft[Acc]((Seq(), start)) {
            case ((slots, lastEnd), appointment) =>
              if(millis(appointment.start) - millis(lastEnd) >= duration)
                (slots :+ TimeSlot(lastEnd, appointment.start), appointment.end)
              else
                (slots, appointment.end)
          }._1
      )
  }
}