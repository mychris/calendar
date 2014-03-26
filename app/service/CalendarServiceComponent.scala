package service

import akka.actor._

import datasource._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._

/**
  *
  * @author Simon Kaltenbacher
  */
trait CalendarServiceComponent {

  self: UserDataAccessComponentImpl with
        CalendarDataAccessComponentImpl =>

  /** Database */
  val db: Database

  /** User data access module accessor */
  val userDataAccessImpl: UserDataAccessModuleImpl

  /** User data access module accessor */
  val calendarDataAccessImpl: CalendarDataAccessModuleImpl

  import userDataAccessImpl._
  import calendarDataAccessImpl._

  /** */
  trait CalendarServiceModule {

    /** */
    object protocol {

      /*
       * Requests
       */

      /** */
      case class GetTagById(id: Int) extends Request

      /** */
      case class GetAppointmentById(id: Int) extends Request

      /** */
      case class GetTagsFromUser(userId: Int) extends Request

      /** */
      case class GetTagsFromAppointment(appointmentId: Int) extends Request

      /** */
      case class GetAppointmentsWithTag(tagId: Int) extends Request

      /** */
      case class AddTag(name: String, priority: Int, userId: Int) extends Request

      /** */
      case class AddAppointment(description: String, start: DateTime, end: DateTime) extends Request

      /** */
      case class RemoveTags(tagIds: Seq[Int]) extends Request

      /** */
      case class RemoveAppointments(appointmentIds: Seq[Int]) extends Request

      /*
       * Reponses
       */

      /** */
      case class TagById(tag: Tag) extends Response

      /** */
      case class AppointmentById(appointment: Appointment) extends Response

      /** */
      case class TagsFromUser(tags: Seq[Tag]) extends Response

      /** */
      case class TagsFromAppointment(tags: Seq[Tag]) extends Response

      /** */
      case class AppointmentsWithTag(appointments: Seq[Appointment]) extends Response

      /** */
      case class TagAdded(id: Int) extends Response

      /** */
      case class AppointmentAdded(id: Int) extends Response

      /** */
      case object TagsRemoved extends Response

      /** */
      case object AppointmentsRemoved extends Response

      /*
       * Errors
       */

      /** */
      case class NoSuchTag(message: String) extends Error

      /** */
      case class NoSuchAppointment(message: String) extends Error
    }

    /** */
    trait PropsFactory {

      /** */
      def calendarService: Props
    }

    /** */
    val factory: PropsFactory

    /** */
    trait CalendarService extends Actor
  }
}

/**
  *
  * @author Simon Kaltenbacher
  */
trait CalendarServiceComponentImpl extends CalendarServiceComponent {

  self: UserDataAccessComponentImpl
  with  CalendarDataAccessComponentImpl =>

  import userDataAccessImpl._
  import calendarDataAccessImpl._

  /** */
  trait CalendarServiceModuleImpl extends CalendarServiceModule {

    import protocol._

    /** */
    object factory extends PropsFactory {

      def calendarService: Props = Props(classOf[CalendarServiceImpl])
    }

    /** */
    class CalendarServiceImpl extends CalendarService with Actor with ActorLogging {

      /** */
      def getTagById(id: Int) =
        db.withSession { implicit session => sender ! tagsById(id).firstOption.fold[Any](NoSuchTag(s"Tag with id $id does not exist!"))(TagById(_)) }

      /** */
      def getAppointmentById(id: Int) =
        db.withSession { implicit session => sender ! appointmentsById(id).firstOption.fold[Any](NoSuchTag(s"Appointment with id $id does not exist!"))(AppointmentById(_)) }

      /** */
      def getTagsFromUser(userId: Int) =
        db.withSession { implicit session => sender ! TagsFromUser(tagsFromUser(userId).buildColl[Seq]) }

      /** */
      def getTagsFromAppointment(appointmentId: Int) =
        db.withSession { implicit session => sender ! TagsFromAppointment(tagsFromAppointment(appointmentId).buildColl[Seq]) }

      /** */
      def getAppointmentsWithTag(tagId: Int) =
        db.withSession { implicit session => sender ! AppointmentsWithTag(appointmentsWithTag(tagId).buildColl[Seq]) }

      /** */
      def addTag(name: String, priority: Int, userId: Int) =
        db.withSession { implicit session => TagAdded((tags returning tags.map(_.id)) += new Tag(-1, name, priority, userId)) }

      /** */
      def addAppointment(description: String, start: DateTime, end: DateTime) =
        db.withSession { implicit session => AppointmentAdded((appointments returning appointments.map(_.id)) += new Appointment(-1, description, start, end)) }

      /** */
      def removeTags(tagIds: Seq[Int]) =
        db.withSession { implicit session =>
          tags.filter(_.id.inSet(tagIds)).delete
          sender ! TagsRemoved
        }

      /** */
      def removeAppointments(appointmentIds: Seq[Int]) =
        db.withSession { implicit session =>
          appointments.filter(_.id.inSet(appointmentIds)).delete
          sender ! AppointmentsRemoved
        }

      def receive =  {
        case GetTagById(id)                          => getTagById(id)
        case GetAppointmentById(id)                  => getAppointmentById(id)
        case GetTagsFromUser(userId)                 => getTagsFromUser(userId)
        case GetTagsFromAppointment(appointmentId)   => getTagsFromAppointment(appointmentId)
        case GetAppointmentsWithTag(tagId)           => getAppointmentsWithTag(tagId)
        case AddTag(name, priority, userId)          => addTag(name, priority, userId)
        case AddAppointment(description, start, end) => addAppointment(description, start, end)
        case RemoveTags(tagIds)                      => removeTags(tagIds)
        case RemoveAppointments(appointmentIds)      => removeAppointments(appointmentIds)
      }
    }
  }
}