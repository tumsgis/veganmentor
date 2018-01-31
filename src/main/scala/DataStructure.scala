import java.time.LocalDateTime

object DataStructure {

  val mentorShipMap = Map("Mentor" -> true, "Lærlingur" -> false)
  val termsAndConditionsApprovement = "Ég samþykki að bla bla bla bla"

  sealed trait Participant {
    def timestamp: LocalDateTime
    def email: String
    def name: String
    def note: String
    def approvedTermsAndConditions: Boolean
  }

  case class Mentor(timestamp: LocalDateTime,
                    email: String,
                    name: String,
                    note: String,
                    approvedTermsAndConditions: Boolean,
                    nrOfAvailableParticipants: Int,
                    mentees: List[Mentee] = List()) extends Participant {

    def assignMentee(mentee: Mentee): Mentor = copy(mentees = mentee :: mentees)
  }

  case class Mentee(timestamp: LocalDateTime,
                    email: String,
                    name: String,
                    note: String,
                    approvedTermsAndConditions: Boolean,
                    mentor: Option[Mentor] = None) extends Participant {

    def assignMentor(mentor: Mentor): Mentee = copy(mentor = Some(mentor))
  }

  case class ParsedInput(mentors: Seq[Mentor],
                         mentees: Seq[Mentee])

  case class PairingResult(mentors: Seq[Mentor],
                           menteesWaitingList: Option[Seq[Mentee]])

  case class ProcessedPairingResult(pairedMentors: Seq[Mentor],
                                    pairedMentees: Seq[Mentee],
                                    mentorsWaiting: Seq[Mentor],
                                    menteesWaiting: Seq[Mentee])
}
