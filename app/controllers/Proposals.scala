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

object Proposals
  extends Controller with
          Restricted with
          ResponseSerialization with
          ExecutionEnvironment with
          ResponseHandling with
          RequestBodyReader {

  def list = Authenticated.async(parse.json) { implicit request =>
    toJsonResult {
      (Services.proposalService ? GetProposalsFromUser(request.user.id)).expecting[ProposalsFromUser]
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
}