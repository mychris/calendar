package service.protocol

import datasource.user._
import datasource.calendar._
import datasource.proposal._
import datasource.proposal.Vote.Vote

import hirondelle.date4j.DateTime

import util._

case class ProposalWithCreatorAndParticipants(proposal: Proposal, creator: User, participants: Seq[User])
case class ProposalTimeWithVotes(proposalTime: ProposalTime, votes: Seq[VoteWithUser])
case class VoteWithUser(vote: Vote, user: User)

/*
 * Requests
 */

case class AddProposal(title: String, color: Color, creatorId: Int) extends Request
case class AddProposalWithTimes(title: String, color: Color, creatorId: Int, participants: Seq[Int], times: Seq[(DateTime, DateTime)]) extends Request
case class AddProposalTime(start: DateTime, end: DateTime, proposalId: Int, participants: Seq[Int], userId: Int) extends Request
case class AddProposalTimeVote(proposalId: Int, proposalTimeId: Int, vote: Vote.Vote, userId: Int) extends Request
case class GetProposalsForUser(userId: Int) extends Request
case class GetProposalTimesFromProposal(proposalId: Int) extends Request
case class RemoveProposal(id: Int) extends Request
case class FinishVote(proposalId: Int, creator: Int, winningTimeIds: Seq[Int])

/*
 * Reponses
 */

case class ProposalAdded(id: Int) extends Success
case class ProposalTimeAdded(id: Int) extends Success
case object ProposalTimeVoteAdded extends Success
case class ProposalsForUser(proposals: Seq[ProposalWithCreatorAndParticipants]) extends Success
case class ProposalTimesFromProposal(proposalTimes: Seq[ProposalTimeWithVotes]) extends Success
case object ProposalRemoved extends Success
case object VoteFinished extends Success

/*
 * Errors
 */
case class NoSuchProposalError(msg: String) extends Error(msg)