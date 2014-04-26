package service.protocol

import datasource.user._
import datasource.calendar._
import datasource.appointmentproposal._
import datasource.appointmentproposal.Vote.Vote

import hirondelle.date4j.DateTime

import util._

case class ProposalWithCreatorAndParticipants(proposal: Proposal, creator: User, participants: Seq[User])
case class ProposalTimeWithVotes(proposalTime: ProposalTime, votes: Seq[VoteWithUser])
case class VoteWithUser(vote: Vote, user: User)

/*
 * Requests
 */

/* Proposal */
case class AddProposal(title: String, userId: Int) extends Request
case class AddProposalWithTimes(title: String, userId: Int, participants: Seq[Int], times: Seq[(DateTime, DateTime)]) extends Request
case class AddProposalTime(start: DateTime, end: DateTime, proposalId: Int, participants: Seq[Int], userId: Int) extends Request
case class AddProposalTimeVote(proposalId: Int, proposalTimeId: Int, vote: Vote.Vote, userId: Int) extends Request
case class GetProposalsForUser(userId: Int) extends Request
case class GetProposalTimesFromProposal(proposalId: Int) extends Request
case class RemoveProposal(id: Int) extends Request

/*
 * Reponses
 */

/* Proposal */
case class ProposalAdded(id: Int) extends Success
case class ProposalTimeAdded(id: Int) extends Success
case object ProposalTimeVoteAdded extends Success
case class ProposalsForUser(proposals: Seq[ProposalWithCreatorAndParticipants]) extends Success
case class ProposalTimesFromProposal(proposalTimes: Seq[ProposalTimeWithVotes]) extends Success
case object ProposalRemoved extends Success