package service.protocol

import datasource.user._
import datasource.calendar._
import datasource.appointmentproposal._

import hirondelle.date4j.DateTime

import util._

case class ProposalTimeWithVotes(proposalTime: ProposalTime, votes: Seq[ProposalTimeVoteVoteWithUser])
case class ProposalTimeVoteVoteWithUser(vote: ProposalTimeVote, user: UserWithoutPassword)
case class ProposalFull(id: Int, title: String, creator: UserWithoutPassword, proposalTimes: Seq[ProposalTimeWithVotes])

/*
 * Requests
 */

/* Proposal */
case class AddProposal(title: String, userId: Int) extends Request
case class AddProposalTime(start: DateTime, end: DateTime, proposalId: Int, participants: Seq[Int], userId: Int) extends Request
case class AddProposalTimeVote(proposalId: Int, proposalTimeId: Int, vote: Vote.Vote, userId: Int) extends Request
case class GetProposalsFromUser(userId: Int) extends Request
case class RemoveProposal(id: Int) extends Request

/*
 * Reponses
 */

/* Proposal */
case class ProposalAdded(id: Int) extends Success
case class ProposalTimeAdded(id: Int) extends Success
case object ProposalTimeVoteAdded extends Success
case class ProposalsFromUser(proposals: Seq[ProposalFull]) extends Success
case object ProposalRemoved extends Success