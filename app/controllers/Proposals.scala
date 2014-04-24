package controllers

import play.api.mvc._

import akka.pattern.ask
import scala.concurrent._

import hirondelle.date4j.DateTime

import formatters._

import service._
import service.protocol._
import datasource.appointmentproposal._
import java.util.TimeZone
import datasource.calendar.{Appointment, Tag}

case class AddProposalRequestBody(title: String)
case class AddProposalTimeRequestBody(start: DateTime, end: DateTime, participants: Seq[Int])
case class AddProposalTimeVoteRequestBody(vote: Vote.Vote)
case class FindFreeTimeSlotsRequestBody(userIds: Seq[Int])

object Proposals
  extends Controller with
          Restricted with
          ResponseSerialization with
          ExecutionEnvironment with
          ResponseHandling with
          RequestBodyReader {

  def list = Authenticated.async(parse.json) { implicit request =>
    toJsonResult {
      (Services.proposalService ? GetProposalsForUser(request.user.id)).expecting[ProposalsForUser]
    }
  }

  def add = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalRequestBody] { addProposal =>
      toJsonResult {
        (Services.proposalService ? AddProposal(
          addProposal.title,
          request.user.id
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

  def findFreeTimeSlots(duration: Int, from: DateTime, to: DateTime, startTime: Option[DateTime], endTime: Option[DateTime]) = 
    Authenticated.async(parse.json) { implicit request =>
      readBody[FindFreeTimeSlotsRequestBody] { findFreeTimeSlots =>
        toJsonResult {
          (Services.freeTimeSlotService ?
            FindFreeTimeSlots(
              duration,
              from,
              to,
              startTime,
              endTime,
              findFreeTimeSlots.userIds
            )
          ).expecting[FreeTimeSlots]
        }
      }
    }
}