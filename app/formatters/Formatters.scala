import controllers._

import datasource.calendar._
import datasource.user._
import datasource.appointmentproposal._

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.data.validation.ValidationError
import play.api.libs.json.{util => _, _}

import service.protocol._

import util._
import util.JsonConversion._

package object formatters {

  /*
   * Base types
   */

  implicit object dateTimeFormat extends Format[DateTime] {
    def writes(o: DateTime): JsValue = o.toString.toJson  // sending time as String

//    def reads(json: JsValue): JsResult[DateTime] = json match { // receiving time as String
//      case JsString(s)  => JsSuccess(new DateTime(s))
//      case _            => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
//    }

//    def writes(o: DateTime): JsValue = o.getMilliseconds(TimeZone.getTimeZone("UTC")).asInstanceOf[Long].toJson  // sending time as Long

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
   * Generic
   */

  implicit def tupleWrites[A : Writes] = new Writes[(A, A)] {
    def writes(o: (A, A)): JsValue = Seq(o._1, o._2).toJson
  }

  /*
   * UserService
   */

  implicit val userFormat = Json.format[User]
  implicit val userWithoutPasswordFormat = Json.format[UserWithoutPassword]

  /* Responses */
  implicit val userByIdFormat = Json.format[UserById]
  implicit val userByNameFormat = Json.format[UserByName]
  implicit val allUsersFormat = Json.format[AllUsers]
  implicit val userAddedFormat = Json.format[UserAdded]


  /*
   * CalendarService
   */

  implicit val tagFormat = Json.format[Tag]
  implicit val appointmentFormat = Json.format[Appointment]

  /* Responses */
  implicit val appointmentWithTagsFormat = Json.format[AppointmentWithTags]
  implicit val appointmentByIdFormat = Json.format[AppointmentById]
  implicit val appointmentsFromTagFormat = Json.format[AppointmentsFromTag]
  implicit val appointmentsFromUserFormat = Json.format[AppointmentsFromUser]
  implicit val appointmentsFromUserWithTagFormat = Json.format[AppointmentsFromUserWithTags]
  implicit val appointmentAddedFormat = Json.format[AppointmentAdded]
  implicit val appointmentUpdatedFormat = Json.format[AppointmentUpdated]
  implicit object appointmentsRemoved extends Writes[AppointmentsRemoved.type] {
    def writes(o: AppointmentsRemoved.type): JsValue = "AppointmentsRemoved".toJson
  }

  implicit val tagByIdFormat = Json.format[TagById]
  implicit val tagsFromUserFormat = Json.format[TagsFromUser]
  implicit val tagsFromAppointmentFormat = Json.format[TagsFromAppointment]
  implicit val tagAddedFormat = Json.format[TagAdded]
  implicit val tagUpdatedFormat = Json.format[TagUpdated]
  implicit object tagsRemoved extends Writes[TagsRemoved.type] {
    def writes(o: TagsRemoved.type): JsValue = "TagsRemoved".toJson
  }
  implicit val colorsFormat = Json.format[Colors]

  /*
   * ProposalService
   */

  implicit val proposalFormat = Json.format[Proposal]
  implicit val proposalTimeFromat = Json.format[ProposalTime]
  implicit val proposalTimeVoteFormat = Json.format[ProposalTimeVote]

  implicit val proposalAddedFormat = Json.format[ProposalAdded]
  implicit val proposalTimeAddedFormat = Json.format[ProposalTimeAdded]
  implicit object proposalTimeVoteAddedFormat extends Writes[ProposalTimeVoteAdded.type] {
    def writes(o: ProposalTimeVoteAdded.type): JsValue = "TimeVoteAdded".toJson
  }

  implicit val proposalTimeVoteVoteWithUser = Json.format[ProposalTimeVoteVoteWithUser]
  implicit val proposalTimeWithVotes = Json.format[ProposalTimeWithVotes]
  implicit val proposalFull = Json.format[ProposalFull]
  implicit val proposalsFromUserFormat = Json.format[ProposalsFromUser]

  /*
   * ConflictFindingService
   */

  /* Responses */
  implicit val conflictsWrites = Json.writes[Conflicts]

  /*
   * FreeTimeSlotFindingService
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

  implicit object schemaDroppedFormatWrites extends Writes[SchemaDropped.type] {
    def writes(o: SchemaDropped.type): JsValue = "SchemaDropped".toJson
  }

  implicit object sampleDataCreatedFormatWrites extends Writes[SampleDataCreated.type] {
    def writes(o: SampleDataCreated.type): JsValue = "SampleDataCreated".toJson
  }

  /*
   * Json request bodies
   */

  /* Appointments */
  implicit val addAppointmentRequestBody = Json.format[AddAppointmentRequestBody]
  implicit val updateAppointmentRequestBody = Json.format[UpdateAppointmentRequestBody]
  implicit val appointmentWithTagsResponseBody = Json.format[AppointmentWithTagsResponseBody]

  /* Tags */
  implicit val addTagRequestBody = Json.format[AddTagRequestBody]
  implicit val updateTagRequestBody = Json.format[UpdateTagRequestBody]

  /* Users */
  implicit val addUserRequestBody = Json.format[AddUserRequestBody]

  /* Proposal */
  implicit val addProposalRequestBody = Json.format[AddProposalRequestBody]
  implicit val addProposalTimeRequestBody = Json.format[AddProposalTimeRequestBody]
  implicit val addProposalTimeVoteRequestBody = Json.format[AddProposalTimeVoteRequestBody]
}