import junit.framework.TestCase
import org.junit.Assert._
import org.junit.{Before, Test}
import MongoDbRepo._

class VeganMentorTest extends TestCase{

  @Before
  override def setUp(): Unit = dropAllParticipants()

  @Test
  def testProcessing01(): Unit = {
    val testFile = "src/test/files/Vegan_mentor_test_01.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val sortedInput = VeganMentor.makeSortedQueue(parsedInput._1, parsedInput._2)
    val pairingResult = VeganMentor.pairParticipants(sortedInput.mentors, sortedInput.mentees)
    val pairingReport = VeganMentor.makePairingReport(pairingResult)

    val pairedMentors = pairingReport.pairedMentors
    val pairedMentees = pairingReport.pairedMentees
    val mentorsWaiting = pairingReport.mentorsWaiting
    val menteesWaiting = pairingReport.menteesWaiting
    val nonApproved = pairingReport.nonApproved

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)
    nonApproved.mentors.foreach(JsonMapping.printMentor)
    nonApproved.mentees.foreach(JsonMapping.printMentee)

    assertEquals(3, pairedMentors.size)
    assertEquals(3, pairedMentees.size)
    assertEquals(0, mentorsWaiting.size)
    assertEquals(2, menteesWaiting.size)
    assertEquals(0, nonApproved.mentors.size)
    assertEquals(0, nonApproved.mentees.size)

    assertEquals(1, pairedMentors.head.mentees.size)
    assertEquals(1, pairedMentors(1).mentees.size)
    assertEquals(1, pairedMentors(2).mentees.size)

    assertEquals(
      "geirlaug@gmail.com taktur@gmail.com",
      s"${pairedMentors.head.email} ${pairedMentors.head.mentees.head.email}"
    )
    assertEquals(
      "hekkipekki@gmail.com blossi@gmail.com",
      s"${pairedMentors(1).email} ${pairedMentors(1).mentees.head.email}"
    )
    assertEquals(
      "andres@gmail.com tvistur@gmail.com",
      s"${pairedMentors(2).email} ${pairedMentors(2).mentees.head.email}"
    )

    assertEquals(
      "nuddi@gmail.com",
      s"${menteesWaiting.head.email}"
    )
    assertEquals(
      "grima@gmail.com",
      s"${menteesWaiting(1).email}"
    )
  }

  @Test
  def testProcessing02 = {
    val testFile = "src/test/files/Vegan_mentor_test_02.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val sortedInput = VeganMentor.makeSortedQueue(parsedInput._1, parsedInput._2)
    val pairingResult = VeganMentor.pairParticipants(sortedInput.mentors, sortedInput.mentees)
    val pairingReport = VeganMentor.makePairingReport(pairingResult)

    val pairedMentors = pairingReport.pairedMentors
    val pairedMentees = pairingReport.pairedMentees
    val mentorsWaiting = pairingReport.mentorsWaiting
    val menteesWaiting = pairingReport.menteesWaiting
    val nonApproved = pairingReport.nonApproved

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)
    nonApproved.mentors.foreach(JsonMapping.printMentor)
    nonApproved.mentees.foreach(JsonMapping.printMentee)

    assertEquals(1, pairedMentors.size)
    assertEquals(1, pairedMentees.size)
    assertEquals(2, mentorsWaiting.size)
    assertEquals(0, menteesWaiting.size)
    assertEquals(0, nonApproved.mentors.size)
    assertEquals(0, nonApproved.mentees.size)

    assertEquals(1, pairedMentors.head.mentees.size)

    assertEquals(
      "geirlaug@gmail.com taktur@gmail.com",
      s"${pairedMentors.head.email} ${pairedMentors.head.mentees.head.email}"
    )

    assertEquals(
      "hekkipekki@gmail.com",
      s"${mentorsWaiting.head.email}"
    )
    assertEquals(
      "andres@gmail.com",
      s"${mentorsWaiting(1).email}"
    )
  }

  /** Scenario having participants that did not approve of terms and conditions */
  @Test
  def testProcessing03 = {
    val testFile = "src/test/files/Vegan_mentor_test_03.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val sortedInput = VeganMentor.makeSortedQueue(parsedInput._1, parsedInput._2)
    val pairingResult = VeganMentor.pairParticipants(sortedInput.mentors, sortedInput.mentees)
    val pairingReport = VeganMentor.makePairingReport(pairingResult)

    val pairedMentors = pairingReport.pairedMentors
    val pairedMentees = pairingReport.pairedMentees
    val mentorsWaiting = pairingReport.mentorsWaiting
    val menteesWaiting = pairingReport.menteesWaiting
    val nonApproved = pairingReport.nonApproved

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)
    nonApproved.mentors.foreach(JsonMapping.printMentor)
    nonApproved.mentees.foreach(JsonMapping.printMentee)

    assertEquals(2, pairedMentors.size)
    assertEquals(5, pairedMentees.size)
    assertEquals(0, mentorsWaiting.size)
    assertEquals(0, menteesWaiting.size)
    assertEquals(2, nonApproved.mentors.size)
    assertEquals(1, nonApproved.mentees.size)

    assertEquals(3, pairedMentors.head.mentees.size)
    assertEquals(2, pairedMentors(1).mentees.size)

    assertEquals(
      "geirlaug@gmail.com grima@gmail.com",
      s"${pairedMentors.head.email} ${pairedMentors.head.mentees.head.email}"
    )
    assertEquals(
      "hekkipekki@gmail.com nuddi@gmail.com",
      s"${pairedMentors(1).email} ${pairedMentors(1).mentees.head.email}"
    )
    assertEquals(
      "geirlaug@gmail.com tvistur@gmail.com",
      s"${pairedMentors.head.email} ${pairedMentors.head.mentees(1).email}"
    )
    assertEquals(
      "hekkipekki@gmail.com blossi@gmail.com",
      s"${pairedMentors(1).email} ${pairedMentors(1).mentees(1).email}"
    )
    assertEquals(
      "geirlaug@gmail.com taktur@gmail.com",
      s"${pairedMentors.head.email} ${pairedMentors.head.mentees(2).email}"
    )
    assertEquals(
      "samthykkiekki@gmail.com samthykkiekki2@gmail.com samthykkiekki3@gmail.com",
      s"${nonApproved.mentees.head.email} ${nonApproved.mentors.head.email} ${nonApproved.mentors(1).email}"
    )
  }
}
