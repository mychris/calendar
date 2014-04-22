package datasource.appointmentproposal

import datasource.user._
import datasource.calendar._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}
import scala.slick.model.ForeignKeyAction
import scala.slick.lifted.Shape

import util.CustomColumnTypes._

import Vote.Vote

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

    type Proposal <: AbstractProposal
    type ProposalTable <: AbstractProposalTable
    type ProposalTime <: AbstractProposalTime
    type ProposalTimeTable <: AbstractProposalTimeTable
    type ProposalTimeVote <: AbstractProposalTimeVote
    type ProposalTimeVoteTable <: AbstractProposalTimeVoteTable

    /*
     * Shapes
     */

    implicit val proposalShape: Shape[_, ProposalTable, Proposal, ProposalTable]
    implicit val proposalTimeShape: Shape[_, ProposalTimeTable, ProposalTime, ProposalTimeTable]
    implicit val proposalTimeVoteShape: Shape[_, ProposalTimeVoteTable, ProposalTimeVote, ProposalTimeVoteTable]

    /*
     * Mapping from Vote to Int and Int to Vote.
     */
    implicit val eliminationPathwayTypeMapper = MappedColumnType.base[Vote, Int](_.id, Vote(_))

    /*
     * Database tables
     */

    trait AbstractProposalTable extends Table[Proposal] {

      def id        : Column[Int]
      def title     : Column[String]
      def creatorId : Column[Int]

      def creator: Query[UserTable, User]
    }

    trait AbstractProposalTimeTable extends Table[ProposalTime] {

      def id         : Column[Int]
      def start      : Column[DateTime]
      def end        : Column[DateTime]
      def proposalId : Column[Int]

      def proposal: Query[ProposalTable, Proposal]
    }

    trait AbstractProposalTimeVoteTable extends Table[ProposalTimeVote] {

      def proposalTimeId : Column[Int]
      def userId         : Column[Int]
      def vote           : Column[Vote]

      // def pk             : Constraint
      def proposalTime   : Query[ProposalTimeTable, ProposalTime]
      def user           : Query[UserTable, User]
    }

    /** Data definition language */
    def proposalDdl = proposals.ddl ++ proposalTimes.ddl ++ proposalTimeVotes.ddl

    /*
     * Queries
     */

    val proposals: TableQuery[ProposalTable]
    val proposalTimes: TableQuery[ProposalTimeTable]
    val proposalTimeVotes: TableQuery[ProposalTimeVoteTable]

    def proposalsFromUser(userId: Int): Query[(ProposalTable), (Proposal)] = 
      for {
        ptv <- proposalTimeVotes
        pt  <- ptv.proposalTime
        u   <- ptv.user
        p   <- pt.proposal
        if (u.id === userId && ptv.userId === userId)
      } yield (p)
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

    type Proposal              = datasource.appointmentproposal.Proposal
    type ProposalTable         = ProposalTableImpl
    type ProposalTime          = datasource.appointmentproposal.ProposalTime
    type ProposalTimeTable     = ProposalTimeTableImpl
    type ProposalTimeVote      = datasource.appointmentproposal.ProposalTimeVote
    type ProposalTimeVoteTable = ProposalTimeVoteTableImpl

    /*
     * Shapes
     */

    implicit val proposalShape: Shape[_, ProposalTable, Proposal, ProposalTable] = implicitly[Shape[_, ProposalTable, Proposal, ProposalTable]]
    implicit val proposalTimeShape: Shape[_, ProposalTimeTable, ProposalTime, ProposalTimeTable] = implicitly[Shape[_, ProposalTimeTable, ProposalTime, ProposalTimeTable]]
    implicit val proposalTimeVoteShape: Shape[_, ProposalTimeVoteTable, ProposalTimeVote, ProposalTimeVoteTable] = implicitly[Shape[_, ProposalTimeVoteTable, ProposalTimeVote, ProposalTimeVoteTable]]

    /*
     * Database tables
     */

    class ProposalTableImpl(tag: scala.slick.lifted.Tag) extends Table[Proposal](tag, "proposal") with AbstractProposalTable {

      def id        = column[Int     ]("id", O.PrimaryKey, O.AutoInc)
      def title     = column[String  ]("title", O.NotNull)
      def creatorId = column[Int     ]("creator_id", O.NotNull)

      def creator = foreignKey("creator_fk", creatorId, users)(_.id, onDelete = ForeignKeyAction.Cascade)

      def * = (id, title, creatorId) <> (Proposal.tupled, Proposal.unapply)
    }

    class ProposalTimeTableImpl(tag: scala.slick.lifted.Tag) extends Table[ProposalTime](tag, "proposal_time") with AbstractProposalTimeTable {

      def id         = column[Int      ]("id", O.PrimaryKey, O.AutoInc)
      def start      = column[DateTime ]("start", O.NotNull)
      def end        = column[DateTime ]("end", O.NotNull)
      def proposalId = column[Int      ]("proposal_id", O.NotNull)

      def proposal = foreignKey("proposal_fk", proposalId, proposals)(_.id, onDelete = ForeignKeyAction.Cascade)

      def * = (id, start, end, proposalId) <> (ProposalTime.tupled, ProposalTime.unapply)
    }

    class ProposalTimeVoteTableImpl(tag: scala.slick.lifted.Tag) extends Table[ProposalTimeVote](tag, "proposal_time_vote") with AbstractProposalTimeVoteTable {

      def proposalTimeId = column[Int ]("proposal_time_id", O.NotNull)
      def userId         = column[Int ]("user_id", O.NotNull)
      def vote           = column[Vote]("vote", O.NotNull)

      // def pk             = primaryKey("pk_proposal_time_belongsto_user", (proposalTimeId, userId))
      def proposalTime   = foreignKey("proposal_time_fk", proposalTimeId, proposalTimes)(_.id, onDelete = ForeignKeyAction.Cascade)
      def user           = foreignKey("user_fk", userId, users)(_.id, onDelete = ForeignKeyAction.Cascade)

      def * = (proposalTimeId, userId, vote) <> (ProposalTimeVote.tupled, ProposalTimeVote.unapply)
    }

    /*
     * Queries
     */

    val proposals = TableQuery[ProposalTable]
    val proposalTimes = TableQuery[ProposalTimeTable]
    val proposalTimeVotes = TableQuery[ProposalTimeVoteTable]
  }
}