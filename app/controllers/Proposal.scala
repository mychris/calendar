package controllers

import play.api.mvc._

import akka.pattern.ask
import scala.concurrent._

import hirondelle.date4j.DateTime

import formatters._

import service._
import service.protocol._
import java.util.TimeZone
import datasource.calendar.{Appointment, Tag}

case class AddProposalRequestBody(title: String)
case class AddProposalTimeRequestBody(start: DateTime, end: DateTime, participants: Seq[Int])

object Proposal
  extends Controller with
          Restricted with
          ResponseSerialization with
          ExecutionEnvironment with
          ResponseHandling with
          RequestBodyReader {

  def list = Action.async {
    Future.successful(Status(501)(""))
  }

  def add = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalRequestBody] { addProposal =>
      toJsonResult {
        (Services.calendarService ? AddProposal(
          addProposal.title,
          request.user.id
        )).expecting[ProposalAdded]
      }
    }
  }

  def addTime(proposalId: Int) = Authenticated.async(parse.json) { implicit request =>
    readBody[AddProposalTimeRequestBody] { addProposalTime =>
      toJsonResult {
        (Services.calendarService ? AddProposalTime(
          addProposalTime.start,
          addProposalTime.end,
          proposalId,
          addProposalTime.participants,
          request.user.id
        )).expecting[ProposalTimeAdded]
      }
    }
  }
}