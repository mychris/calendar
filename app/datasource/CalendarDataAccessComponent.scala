package app.datasource

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.Shape

/**
  *
  * @author Simon Kaltenbacher
  */
trait CalendarDataAccessComponent {

  self: UserDataAccessComponent =>

  /** User data access module accessor */
  val userDataAccess: UserDataAccessModule

  import userDataAccess._

  /** */
  trait CalendarDataAccessModule {

    /*
     * Types
     */

    /** */    
    type Appointment <: AbstractAppointment

    /** */
    type AppointmentTable <: AbstractAppointmentTable

    /** */
    type Tag <: AbstractTag

    /** */
    type TagTable <: AbstractTagTable

    /*
     * Shapes
     */

    /** */
    implicit val appointmentShape: Shape[_, AppointmentTable, Appointment, AppointmentTable]

    /** */
    implicit val tagShape: Shape[_, TagTable, Tag, TagTable]

    /*
     * Data Transfer Objects
     */

    /** */
    trait AbstractAppointment {

      def id          : Int
      def description : String
      def start       : DateTime
      def end         : DateTime
    }    

    /** */
    trait AbstractTag {

      def id   : Int
      def name : String
    }

    /*
     * Database tables
     */

    /** */
    trait AbstractAppointmentTable extends Table[Appointment] {

      def id          : Column[Int]
      def description : Column[String]
      def start       : Column[DateTime]
      def end         : Column[DateTime]

      def tags : Query[TagTable, Tag]
    }

    /** */
    trait AbstractTagTable extends Table[Tag] {

      def id   : Column[Int]
      def name : Column[String]

      def user : Query[UserTable, User]
    }

    /*
     * Queries
     */

    /** */
    val appointments: TableQuery[AppointmentTable]

    /** */
    val tags: TableQuery[TagTable]
  }
}