package service

import org.scalatest._

import akka.actor._
import akka.testkit._

import hirondelle.date4j.DateTime

import service.protocol._
import datasource.calendar._

class FreeTimeSlotFindingServiceSpec(_system: ActorSystem)
  extends TestKit(_system) with
          ImplicitSender with
          WordSpecLike with
          Matchers with
          BeforeAndAfterAll {
  
  def this() = this(ActorSystem("FreeSlotFindingServiceSpec"))

  override def afterAll {
  	TestKit.shutdownActorSystem(system)
  }

  def minutesToMillis(minutes: Int) = minutes * 60 * 1000

  "A free slot finding algorithm" must {

    "send back the interval between start and end if there are no appointments" in {

      val service = system.actorOf(FreeTimeSlotFindingService.props)
      val duration = minutesToMillis(60)
      val start = new DateTime("2014-04-01 00:00")
      val end = new DateTime("2014-04-01 23:00")

      service ! FindFreeTimeSlots(duration, start, end, Seq())
      expectMsg(FreeTimeSlots(Seq(
        TimeSlot(
          new DateTime("2014-04-01 00:00"),
          new DateTime("2014-04-01 23:00")
        )
      )))
    }

    "find all free time slots" in {

      val service = system.actorOf(FreeTimeSlotFindingService.props)
      val duration = minutesToMillis(60)
      val start = new DateTime("2014-04-01 00:00")
      val end = new DateTime("2014-04-01 23:00")
      val appointments = Seq(
        Appointment(-1, "", new DateTime("2014-04-01 10:00"), new DateTime("2014-04-01 12:00")),
        Appointment(-1, "", new DateTime("2014-04-01 16:00"), new DateTime("2014-04-01 18:00"))
      )

      service ! FindFreeTimeSlots(duration, start, end, appointments)
      expectMsg(FreeTimeSlots(Seq(
        TimeSlot(
          new DateTime("2014-04-01 00:00"),
          new DateTime("2014-04-01 10:00")
        ),
        TimeSlot(
          new DateTime("2014-04-01 12:00"),
          new DateTime("2014-04-01 16:00")
        ),
        TimeSlot(
          new DateTime("2014-04-01 18:00"),
          new DateTime("2014-04-01 23:00")
        )
      )))
    }
  }
}