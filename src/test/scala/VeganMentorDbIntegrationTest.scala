import junit.framework.TestCase
import org.junit.{Before, Test}
import org.junit.Assert._
import MongoDbRepo._

class VeganMentorDbIntegrationTest extends TestCase {

  @Before
  override def setUp(): Unit = MongoDbRepo.dropAllParticipants()

  @Test
  def testRealProcessing(): Unit = {
    val testFile4 = "src/test/files/Vegan_mentor_test_04.csv"
    VeganMentor.saveInputFileToDb(testFile4)
    VeganMentor.process
    val testFile5 = "src/test/files/Vegan_mentor_test_05.csv"
    VeganMentor.saveInputFileToDb(testFile5)
    VeganMentor.process
  }

/*  @Test
  def testProcessing(): Unit = {
    val testFile4 = "src/test/files/Vegan_mentor_test_04.csv"

    val parsedInput = Parser.parseInputFile(testFile4)
    val sortedInput = VeganMentor.makeSortedQueue(parsedInput._1, parsedInput._2)
    val pairingResult = VeganMentor.pairParticipants(sortedInput.mentors, sortedInput.mentees)

    // Save all to db
    // Approved
    pairingResult.pairedParticipants.mentors.foreach(saveParticipant)
    pairingResult.pairedParticipants.menteesWaitingList.getOrElse(Seq()).foreach(saveParticipant)
    // Non approved
    pairingResult.nonApproved.mentors.foreach(saveParticipant)
    pairingResult.nonApproved.mentees.foreach(saveParticipant)

    // Fetch mentors from db and map into objects.
    getAllMentors.foreach(m => {
      println(m)
      // Get related mentees from database
      val mentee = getMenteesByMentorId(m.id.get) // Mentor has been saved to database so we can be sure that the id is set
      mentee.foreach(println)
      // Make sure that their count matches with number of related mentees
      val expectedMenteesCount: Int = m.approvedSlots - m.emptySlots
      assertEquals(expectedMenteesCount, mentee.size)
    })

    // Parse another file, relying on data in the database
    val testFile5 = "src/test/files/Vegan_mentor_test_05.csv"
  }*/
}
