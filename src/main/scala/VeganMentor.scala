import DataStructure._

import scala.annotation.tailrec

object VeganMentor {

  def pairParticipants(mentors: Seq[Mentor],
                       mentees: Seq[Mentee]): PairingResult = {
    // Iterate through every mentor, assigning to it 1 mentee per iteration.
    // When the mentors iteration is over, another iteration is started, and again, and again
    // until all mentees have been assigned a mentor (if that's possible that is).
    // This way a fair draft is ensured and the odds of someone getting left out is minimized.
    // Note: Mentor <--> Mentee is a One-to-many relation.
    @tailrec
    def pairParticiPants(mentorsIter: Seq[Mentor],
                         menteesIter: Seq[Mentee],
                         mentorsChanged: Seq[Mentor]): PairingResult =
      (mentorsIter, menteesIter, mentorsChanged) match {
        case (_ , Nil, _) => PairingResult(mentorsChanged, None)
        case (_, _, meChanged) if meChanged.forall(m => m.mentees.lengthCompare(m.nrOfAvailableParticipants) == 0) =>
          PairingResult(meChanged, Some(menteesIter))
        case (Nil, _, _) => pairParticiPants(mentorsChanged, menteesIter, mentorsChanged)
        case (menIter, _, _) if menIter.head.mentees.lengthCompare(menIter.head.nrOfAvailableParticipants) == 0 =>
          pairParticiPants(menIter.tail, menteesIter, mentorsChanged)
        case _ =>  pairParticiPants(
          mentorsIter.tail,
          menteesIter.tail,
          mentorsChanged.map(m => if (m.email == mentorsIter.head.email && m.timestamp == mentorsIter.head.timestamp) m.assignMentee(menteesIter.head) else m).toList)
      }
    pairParticiPants(mentors, mentees, mentors)
  }

  def processPairingResult(pairingResult: PairingResult): ProcessedPairingResult = {
    val pairedMentors: Seq[Mentor] = pairingResult.mentors.filter(_.mentees.nonEmpty)
    val pairedMentees: Seq[Mentee] = pairedMentors.flatMap(p => p.mentees.map(m => m.assignMentor(p)))

    val mentorsWaiting = pairingResult.mentors.filter(_.mentees.isEmpty)
    val menteesWaiting = pairingResult.menteesWaitingList.getOrElse(Seq())

    ProcessedPairingResult(pairedMentors, pairedMentees, mentorsWaiting, menteesWaiting)
  }
}