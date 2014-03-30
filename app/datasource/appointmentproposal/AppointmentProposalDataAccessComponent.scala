package datasource.appointmentproposal

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}
import scala.slick.lifted.Shape

import util.slick._

/**
  *
  * @author Christoph Goettschkes
  */
trait AppointmentProposalDataAccessComponent {
  
  self: UserDataAccessComponent =>

  /** User data access module accessor */
  protected val userDataAccess: UserDataAccessModule

  import userDataAccess._

  trait AppointmentProposalDataAccessModule {

    /*
     * Types
     */

    /** */
    type AppointmentProposal <: AbstractAppointmentProposal

    /** */
    type AppointmentProposalTable <: AbstractAppointmentProposalTable

    /** */
    type AppointmentProposalTime <: AbstractAppointmentProposalTime

    /** */
    type AppointmentProposalTimeTable <: AbstractAppointmentProposalTimeTable

    /*
     * Shapes
     */

    /** */
    implicit val appointmentProposalShape: Shape[_, AppointmentProposalTable, AppointmentProposal, AppointmentProposalTable]

    /** */
    implicit val AppointmentProposalTimeShape: Shape[_, AppointmentProposalTimeTable, AppointmentProposalTime, AppointmentProposalTimeTable]

    /*
     * Mapping from Vote to Int and Int to Vote.
     */
    implicit val eliminationPathwayTypeMapper = MappedColumnType.base[Vote.Value, Int](_.id, Vote(_))

    /*
     * Database tables
     */

    /** */
    trait AbstractAppointmentProposalTable extends Table[AppointmentProposal] {

      def id          : Column[Int]
      def description : Column[String]
      def creatorId   : Column[Int]

      def creator: Query[UserTable, User]
    }

    /** */
    trait AbstractAppointmentProposalTimeTable extends Table[AppointmentProposalTime] {

      def id         : Column[Int]
      def start      : Column[DateTime]
      def end        : Column[DateTime]
      def proposalId : Column[Int]

      def proposal: Query[AppointmentProposalTable, AppointmentProposal]
    }

    /*
     * Queries
     */

    /** */
    val appointmentProposals: TableQuery[AppointmentProposalTable]

    /** */
    val appointmentProposalTimes: TableQuery[AppointmentProposalTimeTable]
  }
}

/**
  *
  * @author Christoph Goettschkes
  */
trait AppointmentProposalDataAccessComponentImpl extends AppointmentProposalDataAccessComponent {
  
  self: UserDataAccessComponent =>

  import userDataAccess._

  trait AppointmentProposalDataAccessModuleImpl extends AppointmentProposalDataAccessModule {

    /*
     * Types
     */

    /** */    
    type AppointmentProposal = datasource.appointmentproposal.AppointmentProposal

    /** */
    type AppointmentProposalTable = AppointmentProposalTableImpl

    /** */
    type AppointmentProposalTime = datasource.appointmentproposal.AppointmentProposalTime

    /** */
    type AppointmentProposalTimeTable = AppointmentProposalTimeTableImpl

    /*
     * Shapes
     */

    /** */
    implicit val appointmentProposalShape: Shape[_, AppointmentProposalTable, AppointmentProposal, AppointmentProposalTable] = implicitly[Shape[_, AppointmentProposalTable, AppointmentProposal, AppointmentProposalTable]]

    /** */
    implicit val AppointmentProposalTimeShape: Shape[_, AppointmentProposalTimeTable, AppointmentProposalTime, AppointmentProposalTimeTable] = implicitly[Shape[_, AppointmentProposalTimeTable, AppointmentProposalTime, AppointmentProposalTimeTable]]

    /*
     * Database tables
     */

    /** */
    class AppointmentProposalTableImpl(tag: scala.slick.lifted.Tag) extends Table[AppointmentProposal](tag, "appointment_proposal") with AbstractAppointmentProposalTable {

      def id          = column[Int     ]("id", O.PrimaryKey, O.AutoInc)
      def description = column[String  ]("description", O.NotNull)
      def creatorId   = column[Int     ]("creator_id", O.NotNull)

      def creator = foreignKey("creator_fk", creatorId, users)(_.id)

      def * = (id, description, creatorId) <> (AppointmentProposal.tupled, AppointmentProposal.unapply)
    }

    /** */
    class AppointmentProposalTimeTableImpl(tag: scala.slick.lifted.Tag) extends Table[AppointmentProposalTime](tag, "appointment_proposal_time") with AbstractAppointmentProposalTimeTable {

      def id         = column[Int      ]("id", O.PrimaryKey, O.AutoInc)
      def start      = column[DateTime ]("start", O.NotNull)
      def end        = column[DateTime ]("end", O.NotNull)
      def proposalId = column[Int      ]("proposal_id", O.NotNull)

      def proposal = foreignKey("proposal_fk", proposalId, appointmentProposals)(_.id)

      def * = (id, start, end, proposalId) <> (AppointmentProposalTime.tupled, AppointmentProposalTime.unapply)
    }

    class AppointmentProposalTimeBelongsToUserImpl(tag: scala.slick.lifted.Tag) extends Table[(Int, Int, Vote.Value)](tag, "appointment_proposal_time_belongsto_user") {

      def appointmentProposalTimeId = column[Int       ]("appointment_proposal_time_id", O.NotNull)
      def userId                    = column[Int       ]("user_id", O.NotNull)
      def vote                      = column[Vote.Value]("vote", O.NotNull)

      def pk                        = primaryKey("pk_appointment_proposal_time_belongsto_user", (appointmentProposalTimeId, userId))
      def appointmentProposalTime   = foreignKey("appointment_proposal_time_fk", appointmentProposalTimeId, appointmentProposalTimes)(_.id)
      def user                      = foreignKey("user_fk", userId, users)(_.id)

      def * = (appointmentProposalTimeId, userId, vote)
    }

    /*
     * Queries
     */

    /** */
    val appointmentProposals = TableQuery[AppointmentProposalTable]

    /** */
    val appointmentProposalTimes = TableQuery[AppointmentProposalTimeTable]

    /** */
    val appointmentProposalTimeBelongsToUser = TableQuery[AppointmentProposalTimeBelongsToUserImpl] 
  }
}