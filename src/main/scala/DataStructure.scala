import java.time.LocalDateTime

import com.mongodb.casbah.Imports

object DataStructure {

  val mentorShipMap = Map("Mentor" -> true, "Lærlingur" -> false)

  sealed trait Participant {
    def timestamp: LocalDateTime
    def email: String
    def name: String
    def note: String
    def approvedTermsAndConditions: Boolean
  }

  case class Mentor(id: Option[Imports.ObjectId],
                    timestamp: LocalDateTime,
                    email: String,
                    name: String,
                    note: String,
                    approvedTermsAndConditions: Boolean,
                    approvedSlots: Int,
                    emptySlots: Int,
                    mentees: List[Mentee] = List()) extends Participant {

    def this(timestamp: LocalDateTime,
             email: String,
             name: String,
             note: String,
             approvedTermsAndConditions: Boolean,
             approvedSlots: Int) =
      this(None, timestamp, email, name, note, approvedTermsAndConditions, approvedSlots, approvedSlots)

    def assignMentee(mentee: Mentee): Mentor = copy(mentees = mentee :: mentees)
  }

  case class Mentee(id: Option[Imports.ObjectId],
                    timestamp: LocalDateTime,
                    email: String,
                    name: String,
                    note: String,
                    approvedTermsAndConditions: Boolean,
                    mentorId: Option[Imports.ObjectId],
                    mentor: Option[Mentor] = None) extends Participant {

    def this(timestamp: LocalDateTime,
             email: String,
             name: String,
             note: String,
             approvedTermsAndConditions: Boolean) =
    this(None, timestamp, email, name, note, approvedTermsAndConditions, None)

    def assignMentor(mentor: Mentor): Mentee = copy(mentor = Some(mentor))
  }

  case class SortedQueue(mentors: Seq[Mentor],
                         mentees: Seq[Mentee])

  case class PairedParticipants(mentors: Seq[Mentor],
                                menteesWaitingList: Option[Seq[Mentee]])

  case class NonApproved(mentors: Seq[Mentor],
                         mentees: Seq[Mentee])

  case class PairingResult(pairedParticipants: PairedParticipants,
                           nonApproved: NonApproved)

  case class PairingReport(pairedMentors: Seq[Mentor],
                           pairedMentees: Seq[Mentee],
                           mentorsWaiting: Seq[Mentor],
                           menteesWaiting: Seq[Mentee],
                           nonApproved: NonApproved)
}
