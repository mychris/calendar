import controllers._

import datasource.calendar._
import datasource.user._
import datasource.proposal._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util._
import util.JsonConversion._

package object formatters {

  /*
   * Generic
   */

  implicit def tupleWrites[A : Writes] = new Writes[(A, A)] {
    def writes(o: (A, A)): JsValue = Seq(o._1, o._2).toJson
  }

  /*
   * Base types
   */
  implicit object dateTimeFormat extends Format[DateTime] {
    def writes(o: DateTime): JsValue = o.toString.toJson  // sending time as String

    def reads(json: JsValue): JsResult[DateTime] = json match { // receiving time as Long
      case JsNumber(ms) => JsSuccess(DateTime.forInstant(ms.toLong, TimeZone.getTimeZone("UTC"))) // Frontend sends millis, not nanos!
      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
    }
  }

  implicit object colorFormat extends Format[Color] {
    def writes(o: Color): JsValue = o.code.toJson

    def reads(json: JsValue): JsResult[Color] = json match {
      case JsString(Color(color)) => JsSuccess(color)
      case _                      => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }

  implicit object voteFormat extends Format[Vote.Vote] {
    def writes(o: Vote.Vote): JsValue = o.id.toJson

    def reads(json: JsValue): JsResult[Vote.Vote] = json match {
      case JsString("NotVoted")  => JsSuccess(Vote.NotVoted)
      case JsString("Accepted")  => JsSuccess(Vote.Accepted)
      case JsString("Refused")   => JsSuccess(Vote.Refused)
      case JsString("Uncertain") => JsSuccess(Vote.Uncertain)
      case _                     => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }

  /*
   * UserService
   */
  implicit val userFormat                = Json.format[User]
  implicit val userWithoutPasswordFormat = Json.format[UserWithoutPassword]

  /* Responses */
  implicit val userByIdFormat   = Json.format[UserById]
  implicit val userByNameFormat = Json.format[UserByName]
  implicit val allUsersFormat   = Json.format[AllUsers]
  implicit val userAddedFormat  = Json.format[UserAdded]


  /*
   * CalendarService
   */
  implicit val tagFormat                 = Json.format[Tag]
  implicit val appointmentFormat         = Json.format[Appointment]
  implicit val appointmentWithTagsFormat = Json.format[AppointmentWithTags]

  /* Responses */
  // Appointment
  implicit val appointmentByIdFormat             = Json.format[AppointmentById]
  implicit val appointmentsFromTagFormat         = Json.format[AppointmentsFromTag]
  implicit val appointmentsFromUserFormat        = Json.format[AppointmentsFromUser]
  implicit val appointmentsFromUserWithTagFormat = Json.format[AppointmentsFromUserWithTags]
  implicit val appointmentAddedFormat            = Json.format[AppointmentAdded]
  implicit val appointmentUpdatedFormat          = Json.format[AppointmentUpdated]
  implicit object appointmentsRemovedFormat extends Writes[AppointmentsRemoved.type] {
    def writes(o: AppointmentsRemoved.type): JsValue = "AppointmentsRemoved".toJson
  }

  // Tag
  implicit val tagByIdFormat             = Json.format[TagById]
  implicit val tagsFromUserFormat        = Json.format[TagsFromUser]
  implicit val tagsFromAppointmentFormat = Json.format[TagsFromAppointment]
  implicit val tagAddedFormat            = Json.format[TagAdded]
  implicit val tagUpdatedFormat          = Json.format[TagUpdated]
  implicit object tagsRemovedFormat extends Writes[TagsRemoved.type] {
    def writes(o: TagsRemoved.type): JsValue = "TagsRemoved".toJson
  }
  implicit val colorsFormat = Json.format[Colors]

  /*
   * ProposalService
   */
  implicit val proposalFormat                           = Json.format[Proposal]
  implicit val propoalTimeFormat                        = Json.format[ProposalTime]
  implicit val proposalWithCreatorAndParticipantsFormat = Json.format[ProposalWithCreatorAndParticipants]
  implicit val voteWithUserFormat                       = Json.format[VoteWithUser]
  implicit val proposalTimeWithVotesFormat              = Json.format[ProposalTimeWithVotes]

   /* Responses */

  implicit val proposalAddedFormat     = Json.format[ProposalAdded]
  implicit val proposalTimeAddedFormat = Json.format[ProposalTimeAdded]
  implicit object proposalTimeVoteAddedFormat extends Writes[ProposalTimeVoteAdded.type] {
    def writes(o: ProposalTimeVoteAdded.type): JsValue = "TimeVoteAdded".toJson
  }
  implicit val proposalsForUserFormat          = Json.format[ProposalsForUser]
  implicit val proposalTimesFromProposalFormat = Json.format[ProposalTimesFromProposal]
  implicit object proposalRemovedFormat extends Writes[ProposalRemoved.type] {
    def writes(o: ProposalRemoved.type): JsValue = "ProposalRemoved".toJson
  }
  implicit object voteFinishedFormat extends Writes[VoteFinished.type] {
    def writes(o: VoteFinished.type): JsValue = "VoteFinished".toJson
  }

  /*
   * ConflictFindingService
   */

  /* Responses */
  implicit val conflictsFormat = Json.writes[Conflicts]

  /*
   * FreeTimeSlotService
   */
  implicit val timeSlotFormat = Json.format[TimeSlot]

  /* Responses */
  implicit val freeTimeSlotsFormat = Json.format[FreeTimeSlots]


  /*
   * Exception
   */
  implicit object exceptionWrites extends Writes[Exception] {
    def writes(o: Exception): JsValue = o.getMessage.toJson
  }

  /*
   * AdministrationService
   */
  implicit object schemaCreatedWrites extends Writes[SchemaCreated.type] {
    def writes(o: SchemaCreated.type): JsValue = "SchemaCreated".toJson
  }

  implicit object schemaDroppedWrites extends Writes[SchemaDropped.type] {
    def writes(o: SchemaDropped.type): JsValue = "SchemaDropped".toJson
  }

  implicit object sampleDataCreatedWrites extends Writes[SampleDataCreated.type] {
    def writes(o: SampleDataCreated.type): JsValue = "SampleDataCreated".toJson
  }


  /*
   * Json request bodies
   */
  /* Appointments */
  implicit val addAppointmentRequestBodyFormat       = Json.format[AddAppointmentRequestBody]
  implicit val updateAppointmentRequestBodyFormat    = Json.format[UpdateAppointmentRequestBody]
  implicit val appointmentWithTagsResponseBodyFormat = Json.format[AppointmentWithTagsResponseBody]

  /* Tags */
  implicit val addTagRequestBodyFormat    = Json.format[AddTagRequestBody]
  implicit val updateTagRequestBodyFormat = Json.format[UpdateTagRequestBody]

  /* Users */
  implicit val addUserRequestBodyFormat = Json.format[AddUserRequestBody]

  /* Proposal */
  implicit val addProposalRequestBodyFormat                        = Json.format[AddProposalRequestBody]
  implicit val addProposalTimeRequestBodyFormat                    = Json.format[AddProposalTimeRequestBody]
  implicit val addProposalTimeVoteRequestBodyFormat                = Json.format[AddProposalTimeVoteRequestBody]
  implicit val addProposalTimeWithoutParticipantsRequestBodyFormat = Json.format[AddProposalTimeWithoutParticipantsRequestBody]
  implicit val addProposalWithTimesRequestBodyFormat               = Json.format[AddProposalWithTimesRequestBody]
  implicit val finishVoteRequestBodyFormat                         = Json.format[FinishVoteRequestBody]

}