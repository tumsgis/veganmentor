import DataStructure._

import scala.annotation.tailrec

object VeganMentor {

  def saveInputFileToDb(csvFilePath: String): Unit = {
    val (mentors, mentees) = Parser.parseInputFile(csvFilePath)
    mentors.foreach(MongoDbRepo.saveParticipant)
    mentees.foreach(MongoDbRepo.saveParticipant)
  }

  def process: Unit = {
    val mentors = MongoDbRepo.getMentorsWithEmptySlots
    val mentees = MongoDbRepo.getMenteesSeekingMentor
    val sortedQueue = makeSortedQueue(mentors, mentees)
    val pairingResult = pairParticipants(sortedQueue.mentors, sortedQueue.mentees)
    pairingResult.pairedParticipants.mentors.foreach(MongoDbRepo.updateParticipant)
  }

  def pairParticipants(mentors: Seq[Mentor],
                       mentees: Seq[Mentee]): PairingResult = {

    def mentorSlotFull(mentor: Mentor): Boolean =
      if (mentor.emptySlots == 0) true
      else if (mentor.approvedSlots == mentor.emptySlots) mentor.mentees.size - mentor.approvedSlots == 0
      else mentor.mentees.size - mentor.emptySlots == 0

    // Iterate through every mentor, assigning to it 1 mentee per iteration.
    // When the mentors iteration is over, another iteration is started, and again, and again
    // until all mentees have been assigned a mentor (if that's possible that is).
    // This way a fair draft is ensured and the odds of someone getting left out is minimized.
    // Note: Mentor <--> Mentee is a One-to-many relation.
    @tailrec
    def pairParticiPants(mentorsIter: Seq[Mentor],
                         menteesIter: Seq[Mentee],
                         mentorsChanged: Seq[Mentor]): PairedParticipants =
      (mentorsIter, menteesIter, mentorsChanged) match {
        // All mentees have been assigned a mentor
        case (_ , Nil, _) => PairedParticipants(mentorsChanged, None)
        // All mentor slots are full
        case (_, _, meChanged) if meChanged.forall(mentorSlotFull) => PairedParticipants(meChanged, Some(menteesIter))
        // Another iteration through mentors is needed
        case (Nil, _, _) => pairParticiPants(mentorsChanged, menteesIter, mentorsChanged)
        // The first mentor in the iteration list has all the slots full
        case (menIter, _, _) if mentorSlotFull(menIter.head) => pairParticiPants(menIter.tail, menteesIter, mentorsChanged)
        // Assign mentee to a mentor
        case _ =>  pairParticiPants(
          mentorsIter.tail,
          menteesIter.tail,
          mentorsChanged.map(m => if (m.email == mentorsIter.head.email && m.timestamp == mentorsIter.head.timestamp) m.assignMentee(menteesIter.head) else m).toList)
      }

    val nonApproved: NonApproved = NonApproved(mentors.filter(!_.approvedTermsAndConditions), mentees.filter(!_.approvedTermsAndConditions))
    val approved = (mentors.filter(_.approvedTermsAndConditions), mentees.filter(_.approvedTermsAndConditions))
    PairingResult(pairParticiPants(approved._1, approved._2, approved._1), nonApproved)
  }

  def makePairingReport(pairingResult: PairingResult): PairingReport = {
    val pairedMentors: Seq[Mentor] = pairingResult.pairedParticipants.mentors.filter(_.mentees.nonEmpty)
    val pairedMentees: Seq[Mentee] = pairedMentors.flatMap(p => p.mentees.map(m => m.assignMentor(p)))

    val mentorsWaiting = pairingResult.pairedParticipants.mentors.filter(_.mentees.isEmpty)
    val menteesWaiting = pairingResult.pairedParticipants.menteesWaitingList.getOrElse(Seq())

    PairingReport(pairedMentors, pairedMentees, mentorsWaiting, menteesWaiting, pairingResult.nonApproved)
  }

  def makeSortedQueue (mentorsUnordered: Seq[Mentor], menteesUnOrdered: Seq[Mentee]): SortedQueue = {
    // Erasing duplicates and then sorting.
    val mentorsOrdered = mentorsUnordered
      .groupBy(_.email)
      .map(m => m._2.head).toList
      .sortWith((m1, m2) => m1.emptySlots < m2.emptySlots)
      .sortWith((m1, m2) => m1.timestamp.isBefore (m2.timestamp))
    val menteesOrdered = menteesUnOrdered
      .groupBy (_.email)
      .map(m => m._2.head).toList
      .sortWith((m1, m2) => m1.timestamp.isBefore (m2.timestamp) )

    SortedQueue (mentorsOrdered, menteesOrdered)
  }
}