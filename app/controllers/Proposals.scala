package controllers

import akka.pattern.ask

import datasource.calendar._
import datasource.proposal._

import formatters._

import java.util.TimeZone

import scala.concurrent.{util => _, _}

import hirondelle.date4j.DateTime

import play.api.mvc._

import service._
import service.protocol._

import util.Color

case class AddProposalRequestBody(title: String, color: Color)
case class AddProposalWithTimesRequestBody(title: String, color: Color, participants: Seq[Int], times: Seq[AddProposalTimeWithoutParticipantsRequestBody])
case class AddProposalTimeWithoutParticipantsRequestBody(start: DateTime, end: DateTime)
case class AddProposalTimeRequestBody(start: DateTime, end: DateTime, participants: Seq[Int])
case class AddProposalTimeVoteRequestBody(vote: Vote.Vote)
case class FinishVoteRequestBody(times: Seq[Int])

object Proposals
  extends Controller with
          Restricted with
          ResponseSerialization with
          ExecutionEnvironment with
          ResponseHandling with
          RequestBodyReader {

  def list = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.proposalService ? GetProposalsForUser(request.user.id)).expecting[ProposalsForUser]
    }
  }

  def proposalTimesFromProposal(proposalId: Int) = Authenticated.async(parse.json) { implicit request =>
    toJsonResult {
      (Services.proposalService ? GetProposalTimesFromProposal(proposalId)).expecting[ProposalTimesFromProposal]
    }
  }

  def add = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalRequestBody] { addProposal =>
      toJsonResult {
        (Services.proposalService ? AddProposal(
          addProposal.title,
          addProposal.color,
          request.user.id
        )).expecting[ProposalAdded]
      }
    }
  }

  def addWithTimes = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalWithTimesRequestBody] { addProposal =>
      val requester = request.user.id
      val participants =
        if
          (addProposal.participants.exists(_ == requester)) addProposal.participants
        else
          requester +: addProposal.participants

      toJsonResult {
        (Services.proposalService ? AddProposalWithTimes(
          addProposal.title,
          addProposal.color,
          requester,
          participants,
          addProposal.times.map(p => (p.start, p.end))
        )).expecting[ProposalAdded]
      }
    }
  }

  def addTime(proposalId: Int) = Authenticated.async(parse.json) { implicit request =>
    val requester = request.user.id
    readBody[AddProposalTimeRequestBody] { addProposalTime =>
      toJsonResult {
        (Services.proposalService ? AddProposalTime(
          addProposalTime.start,
          addProposalTime.end,
          proposalId,
          (
            if (addProposalTime.participants.exists(_ == requester))
              addProposalTime.participants
            else
              (requester +: addProposalTime.participants)
          ),
          requester
        )).expecting[ProposalTimeAdded]
      }
    }
  }

  def addVote(proposalId: Int, timeId: Int) = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalTimeVoteRequestBody] { addProposalTimeVote =>
      toJsonResult {
        (Services.proposalService ? AddProposalTimeVote(
          proposalId,
          timeId,
          addProposalTimeVote.vote,
          request.user.id
        )).expecting[ProposalTimeVoteAdded.type]
      }
    }
  }

  def delete(id: Int) = Authenticated.async { implicit request =>
    toJsonResult {
      (Services.proposalService ? RemoveProposal(id)).expecting[ProposalRemoved.type]
    }
  }

  def finishVote(id: Int) = Authenticated.async(parse.json) { implicit request =>
    readBody[FinishVoteRequestBody] { finishVote =>
      toJsonResult {
        (Services.proposalService ? FinishVote(
          id,
          1,
          finishVote.times
        )).expecting[VoteFinished.type]
      }
    }
  }

  def findFreeTimeSlots(userIds: Seq[Int], duration: Int, from: DateTime, to: DateTime, startTime: Option[DateTime], endTime: Option[DateTime]) =
    Authenticated.async { implicit request =>
      toJsonResult {
        (Services.freeTimeSlotService ?
          FindFreeTimeSlots(
            userIds,
            duration,
            from,
            to,
            startTime,
            endTime
          )
        ).expecting[FreeTimeSlots]
      }
    }
}