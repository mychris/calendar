package datasource

import util.slick._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}
import scala.slick.lifted.Shape

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

    object Vote extends Enumeration {
      type Vote = Value
      val NotVoted = Value(0)
      val Accepted = Value(1)
      val Refused  = Value(2)
      val Uncertain = Value(3)
    }

    /*
     * Mapping from Vote to Int and Int to Vote.
     */
    implicit val eliminationPathwayTypeMapper = MappedColumnType.base[Vote.Value, Int](_.id, Vote(_))

    /*
     * Shapes
     */

    /** */
    implicit val appointmentProposalShape: Shape[_, AppointmentProposalTable, AppointmentProposal, AppointmentProposalTable]

    /** */
    implicit val AppointmentProposalTimeShape: Shape[_, AppointmentProposalTimeTable, AppointmentProposalTime, AppointmentProposalTimeTable]


    /*
     * Data Transfer Objects
     */

    /** */
    trait AbstractAppointmentProposal {

      def id          : Int
      def description : String
      def creatorId   : Int
    }

    /** */
    trait AbstractAppointmentProposalTime {

      def id         : Int
      def start      : DateTime
      def end        : DateTime
      def proposalId : Int
    }

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
    type AppointmentProposal = AppointmentProposalImpl

    /** */
    type AppointmentProposalTable = AppointmentProposalTableImpl

    /** */
    type AppointmentProposalTime = AppointmentProposalTimeImpl

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
     * Data Transfer Objects
     */

    /** */
    protected case class AppointmentProposalImpl(id: Int, description: String, creatorId: Int) extends AbstractAppointmentProposal

    /** */
    protected case class AppointmentProposalTimeImpl(id: Int, start: DateTime, end: DateTime, proposalId: Int) extends AbstractAppointmentProposalTime

    /*
     * Database tables
     */

    /** */
    protected class AppointmentProposalTableImpl(tag: scala.slick.lifted.Tag) extends Table[AppointmentProposal](tag, "appointment_proposal") with AbstractAppointmentProposalTable {

      def id          = column[Int     ]("id", O.PrimaryKey, O.AutoInc)
      def description = column[String  ]("description", O.NotNull)
      def creatorId   = column[Int     ]("creator_id", O.NotNull)

      def creator = foreignKey("creator_fk", creatorId, users)(_.id)

      def * = (id, description, creatorId) <> (AppointmentProposalImpl.tupled, AppointmentProposalImpl.unapply)
    }

    /** */
    protected class AppointmentProposalTimeTableImpl(tag: scala.slick.lifted.Tag) extends Table[AppointmentProposalTime](tag, "appointment_proposal_time") with AbstractAppointmentProposalTimeTable {

      def id         = column[Int      ]("id", O.PrimaryKey, O.AutoInc)
      def start      = column[DateTime ]("start", O.NotNull)
      def end        = column[DateTime ]("end", O.NotNull)
      def proposalId = column[Int      ]("proposal_id", O.NotNull)

      def proposal = foreignKey("proposal_fk", proposalId, appointmentProposals)(_.id)

      def * = (id, start, end, proposalId) <> (AppointmentProposalTimeImpl.tupled, AppointmentProposalTimeImpl.unapply)
    }

    protected class AppointmentProposalTimeBelongsToUserImpl(tag: scala.slick.lifted.Tag) extends Table[(Int, Int, Vote.Value)](tag, "appointment_proposal_time_belongsto_user") {

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