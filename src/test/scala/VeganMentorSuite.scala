import junit.framework.TestCase
import org.junit.Assert._
import org.junit.Test


class VeganMentorSuite extends TestCase{

  @Test
  def testProcessing01 = {
    val testFile = "src/files/Vegan_mentor_test_01.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val pairedParticipants = VeganMentor.pairParticipants(parsedInput.mentors, parsedInput.mentees)
    val processedResults = VeganMentor.processPairingResult(pairedParticipants)

    val pairedMentors = processedResults.pairedMentors
    val pairedMentees = processedResults.pairedMentees
    val mentorsWaiting = processedResults.mentorsWaiting
    val menteesWaiting = processedResults.menteesWaiting

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)

    assertEquals(3, pairedMentors.size)
    assertEquals(3, pairedMentees.size)
    assertEquals(0, mentorsWaiting.size)
    assertEquals(2, menteesWaiting.size)

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
    val testFile = "src/files/Vegan_mentor_test_02.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val pairedParticipants = VeganMentor.pairParticipants(parsedInput.mentors, parsedInput.mentees)
    val processedResults = VeganMentor.processPairingResult(pairedParticipants)

    val pairedMentors = processedResults.pairedMentors
    val pairedMentees = processedResults.pairedMentees
    val mentorsWaiting = processedResults.mentorsWaiting
    val menteesWaiting = processedResults.menteesWaiting

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)

    assertEquals(1, pairedMentors.size)
    assertEquals(1, pairedMentees.size)
    assertEquals(2, mentorsWaiting.size)
    assertEquals(0, menteesWaiting.size)

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

  @Test
  def testProcessing03 = {
    val testFile = "src/files/Vegan_mentor_test_03.csv"

    val parsedInput = Parser.parseInputFile(testFile)
    val pairedParticipants = VeganMentor.pairParticipants(parsedInput.mentors, parsedInput.mentees)
    val processedResults = VeganMentor.processPairingResult(pairedParticipants)

    val pairedMentors = processedResults.pairedMentors
    val pairedMentees = processedResults.pairedMentees
    val mentorsWaiting = processedResults.mentorsWaiting
    val menteesWaiting = processedResults.menteesWaiting

    pairedMentors.foreach(JsonMapping.printMentor)
    pairedMentees.foreach(JsonMapping.printMentee)
    mentorsWaiting.foreach(JsonMapping.printMentor)
    menteesWaiting.foreach(JsonMapping.printMentee)

    assertEquals(2, pairedMentors.size)
    assertEquals(5, pairedMentees.size)
    assertEquals(0, mentorsWaiting.size)
    assertEquals(0, menteesWaiting.size)

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
  }
}
