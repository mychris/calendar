package datasource.proposal

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}

import com.github.nscala_time.time.Imports._

import util._

object Vote extends Enumeration {

  type Vote = Value

  val NotVoted  = Value(0)
  val Accepted  = Value(1)
  val Refused   = Value(2)
  val Uncertain = Value(3)
}

import Vote.Vote

trait AbstractProposal {

  def id        : Int
  def title     : String
  def creatorId : Int
}

trait AbstractProposalTime {

  def id         : Int
  def start      : DateTime
  def end        : DateTime
  def proposalId : Int
}

trait AbstractProposalTimeVote {

  def proposalTimeId : Int
  def userId         : Int
  def vote           : Vote
}

case class Proposal(id: Int, title: String, color:Color, creatorId: Int) extends AbstractProposal
case class ProposalTime(id: Int, start: DateTime, end: DateTime, proposalId: Int) extends AbstractProposalTime
case class ProposalTimeVote(proposalTimeId: Int, userId: Int, vote: Vote) extends AbstractProposalTimeVote