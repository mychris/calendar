package app.datasource

import app.util.slick._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}
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

      def id       : Int
      def name     : String
      def priority : Int
      def userId   : Int
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

      def id       : Column[Int]
      def name     : Column[String]
      def priority : Column[Int]
      def userId   : Column[Int]

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

/**
  *
  * @author Simon Kaltenbacher
  */
trait CalendarDataAccessComponentImpl extends CalendarDataAccessComponent {

  self: UserDataAccessComponent =>

  import userDataAccess._

  /** */
  trait CalendarDataAccessModuleImpl extends CalendarDataAccessModule {

    /*
     * Types
     */

    /** */    
    type Appointment = AppointmentImpl

    /** */
    type AppointmentTable = AppointmentTableImpl

    /** */
    type Tag = TagImpl

    /** */
    type TagTable = TagTableImpl

    /*
     * Shapes
     */

    /** */
    implicit val appointmentShape: Shape[_, AppointmentTable, Appointment, AppointmentTable] = implicitly[Shape[_, AppointmentTable, Appointment, AppointmentTable]]

    /** */
    implicit val tagShape: Shape[_, TagTable, Tag, TagTable] = implicitly[Shape[_, TagTable, Tag, TagTable]]

    /*
     * Data Transfer Objects
     */

    /** */
    protected case class AppointmentImpl(id: Int, description: String, start: DateTime, end: DateTime) extends AbstractAppointment

    /** */
    protected case class TagImpl(id: Int, name: String, priority: Int, userId: Int) extends AbstractTag

    /*
     * Database tables
     */

    /** */
    protected class AppointmentTableImpl(tag: scala.slick.lifted.Tag) extends Table[Appointment](tag, "appointment") with AbstractAppointmentTable {

      def id          = column[Int     ]("id", O.PrimaryKey, O.AutoInc)
      def description = column[String  ]("description", O.NotNull)
      def start       = column[DateTime]("start_date", O.NotNull)
      def end         = column[DateTime]("end_date", O.NotNull)

      def tags        = for(abtt <- appointmentBelongsToTag; t <- abtt.tag if abtt.appointmentId == id) yield t

      def *           = (id, description, start, end) <> (AppointmentImpl.tupled, AppointmentImpl.unapply)
    }

    /** */
    protected class TagTableImpl(tag: scala.slick.lifted.Tag) extends Table[Tag](tag, "tag") with AbstractTagTable {

      def id       = column[Int   ]("id", O.PrimaryKey, O.AutoInc)
      def name     = column[String]("name", O.NotNull)
      def priority = column[Int   ]("priority", O.NotNull)
      def userId   = column[Int   ]("user_id", O.NotNull)

      def user   = foreignKey("user_fk", userId, users)(_.id)

      def *      = (id, name, priority, userId) <> (TagImpl.tupled, TagImpl.unapply)
    }

    /** */
    protected class AppointmentBelongsToTagTableImpl(tag: scala.slick.lifted.Tag) extends Table[(Int, Int)](tag, "appointment_belongsto_tag") {

      def appointmentId = column[Int]("appointment_id")
      def tagId         = column[Int]("tag_id")

      def pk            = primaryKey("pk_appointment_belongsto_tag", (appointmentId, tagId))
      def appointment   = foreignKey("appointment_fk", appointmentId, appointments)(_.id)
      def tag           = foreignKey("tag_fk", tagId, tags)(_.id)

      def *             = (appointmentId, tagId)
    }

    /*
     * Queries
     */

    /** */
    val appointments = TableQuery[AppointmentTable]

    /** */
    val tags = TableQuery[TagTable]

    /** */
    val appointmentBelongsToTag = TableQuery[AppointmentBelongsToTagTableImpl] 
  }
}