package service

import org.scalatest._

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import akka.testkit.ImplicitSender

import hirondelle.date4j.DateTime

import service.protocol._
import datasource.calendar.Appointment

class ConflictFindingServiceSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  def this() = this(ActorSystem("ConflictFindingServiceSpec"))

  override def afterAll {
  	TestKit.shutdownActorSystem(system)
  }

  "A Conflict finding service" must {

    "send back a message if there are no appointments" in {
      val service = system.actorOf(ConflictFindingService.props)
      service ! FindConflicts(Nil)
      expectMsg(Conflicts(Nil))
    }

    "send back an empty list if there is only on appointment" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments = Appointment(0, "", new DateTime("2014-04-01"), new DateTime("2014-04-01")) :: Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(Nil))
    }

    "send back an empty list if there are no conflicts" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")) :: 
        Appointment(0, "", new DateTime("2014-04-01 02:00:00"), new DateTime("2014-04-01 03:00:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 03:00:00"), new DateTime("2014-04-01 04:00:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(Nil))
    }

    "send back an empty list if there are no conflicts in an unsorted list" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "", new DateTime("2014-04-01 02:00:00"), new DateTime("2014-04-01 03:00:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 03:00:00"), new DateTime("2014-04-01 04:00:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(Nil))
    }

    "send back a conflict if two Appointments start and end at the same point in time" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "first", new DateTime("2014-04-01 00:00:00"), new DateTime("2014-04-01 03:00:00")) ::
        Appointment(0, "second", new DateTime("2014-04-01 00:00:00"), new DateTime("2014-04-01 03:00:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(
        (
          Appointment(0, "first", new DateTime("2014-04-01 00:00:00"), new DateTime("2014-04-01 03:00:00")),
          Appointment(0, "second", new DateTime("2014-04-01 00:00:00"), new DateTime("2014-04-01 03:00:00"))
        ) ::
        Nil
      ))
    }

    "send back the conflicts if there are any #1" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 01:55:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(
        (
          Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")),
          Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 01:55:00"))
        ) ::
        Nil
      ))
    }

    "send back the conflicts if there are any #2" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 03:00:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(
        (
          Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")),
          Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 03:00:00"))
        ) ::
        Nil
      ))
    }

    "send back the conflicts if there are any #3" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments =
        Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 01:55:00")) ::
        Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")) ::
        Nil
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(
        (
          Appointment(0, "", new DateTime("2014-04-01 01:00:00"), new DateTime("2014-04-01 02:00:00")),
          Appointment(0, "", new DateTime("2014-04-01 01:50:00"), new DateTime("2014-04-01 01:55:00"))
        ) ::
        Nil
      ))
    }

    "be able to operate on large lists" in {
      val service = system.actorOf(ConflictFindingService.props)
      var appointments = Appointment(0, "", new DateTime("2014-04-01 00:00:00"), new DateTime("2014-04-01 05:00:00")) :: Nil
      for (i <- 1 to 50000) {
        val start = appointments.head.start.plusDays(1)
        val end = appointments.head.end.plusDays(1)
        appointments = Appointment(0, "", start, end) :: appointments
      }
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(Nil))
    }

    "be able to find all conflicts even if one appointment spans several others" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments = Seq(
        Appointment(0, "", new DateTime("2014-01-01 08:00:00"), new DateTime("2014-01-01 18:00")),
        Appointment(0, "", new DateTime("2014-01-01 09:00:00"), new DateTime("2014-01-01 11:00")),
        Appointment(0, "", new DateTime("2014-01-01 13:00:00"), new DateTime("2014-01-01 14:00"))
      )
      service ! FindConflicts(appointments)
      expectMsg(Conflicts(Seq(
        (
          Appointment(0, "", new DateTime("2014-01-01 08:00:00"), new DateTime("2014-01-01 18:00")),
          Appointment(0, "", new DateTime("2014-01-01 09:00:00"), new DateTime("2014-01-01 11:00"))
        ),
        (
          Appointment(0, "", new DateTime("2014-01-01 08:00:00"), new DateTime("2014-01-01 18:00")),
          Appointment(0, "", new DateTime("2014-01-01 13:00:00"), new DateTime("2014-01-01 14:00"))
        )
      )))
    }

    "A" in {
      val service = system.actorOf(ConflictFindingService.props)
      val appointments = Seq(
        Appointment(0, "Q", new DateTime("2014-01-01 13:30:00"), new DateTime("2014-01-01 17:30")),
        Appointment(0, "D", new DateTime("2014-01-01 14:00:00"), new DateTime("2014-01-01 15:30")),
        Appointment(0, "A", new DateTime("2014-01-01 15:00:00"), new DateTime("2014-01-01 16:00")),
        Appointment(0, "C", new DateTime("2014-01-01 15:30:00"), new DateTime("2014-01-01 17:00"))
      )

      service ! FindConflicts(appointments)

      expectMsg(Conflicts(Seq(
          (
            Appointment(0, "Q", new DateTime("2014-01-01 13:30:00"), new DateTime("2014-01-01 17:30")),
            Appointment(0, "D", new DateTime("2014-01-01 14:00:00"), new DateTime("2014-01-01 15:30"))
          ),
          (
            Appointment(0, "Q", new DateTime("2014-01-01 13:30:00"), new DateTime("2014-01-01 17:30")),
            Appointment(0, "A", new DateTime("2014-01-01 15:00:00"), new DateTime("2014-01-01 16:00"))
          ),
          (
            Appointment(0, "Q", new DateTime("2014-01-01 13:30:00"), new DateTime("2014-01-01 17:30")),
            Appointment(0, "C", new DateTime("2014-01-01 15:30:00"), new DateTime("2014-01-01 17:00"))
          ),
          (
            Appointment(0, "D", new DateTime("2014-01-01 14:00:00"), new DateTime("2014-01-01 15:30")),
            Appointment(0, "A", new DateTime("2014-01-01 15:00:00"), new DateTime("2014-01-01 16:00"))
          ),
          (
            Appointment(0, "A", new DateTime("2014-01-01 15:00:00"), new DateTime("2014-01-01 16:00")),
            Appointment(0, "C", new DateTime("2014-01-01 15:30:00"), new DateTime("2014-01-01 17:00"))
          )
        )))
    }

  }
}