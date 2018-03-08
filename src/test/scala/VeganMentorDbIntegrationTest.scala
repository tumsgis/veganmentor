import java.time.LocalDateTime

import DataStructure.{Mentee, Mentor}
import junit.framework.TestCase
import org.junit.Assert._
import org.junit.{After, Before, Test}


class VeganMentorDbIntegrationTest extends TestCase {

  @Before
  override def setUp(): Unit = MongoDbRepo.dropAllParticipants()

  @After
  override def tearDown(): Unit = assertTrue("Some Mentor empty slots are below zero", TestUtil.noEmptySlotsBelowZero)

  private object testData {
    val timestamp = LocalDateTime.now
    val email = "test@test.com"
    val email2 = "test2@test2.com"
    val name = "name"
    val note = "note"
    val approvedTermsAndConditions = true
    val approvedSlots = 2
  }

  @Test
  def testSave(): Unit = {
    import testData._

    val mentor: Mentor = new Mentor(timestamp, email, name, note, approvedTermsAndConditions, approvedSlots)
    MongoDbRepo.saveParticipant(mentor)
    MongoDbRepo.getMentorByEmail("test@test.com").foreach(m => {
      assertNotEquals(None, m.id)
      assertEquals(timestamp, m.timestamp)
      assertEquals(email, m.email)
      assertEquals(name, m.name)
      assertEquals(note, m.note)
      assertTrue(approvedTermsAndConditions)
      assertEquals(approvedSlots, m.approvedSlots)
      assertEquals(approvedSlots, m.emptySlots)
    })
  }

  @Test
  def testProcessAndUpdate(): Unit = {
    import testData._

    val mentee: Mentee = new Mentee(timestamp, email, name, note, approvedTermsAndConditions)
    MongoDbRepo.saveParticipant(mentee)
    MongoDbRepo.getMenteeByEmail("test@test.com").foreach(m => {
      assertNotEquals(None, m.id)
      assertEquals(timestamp, m.timestamp)
      assertEquals(email, m.email)
      assertEquals(name, m.name)
      assertEquals(note, m.note)
      assertTrue(approvedTermsAndConditions)
      assertEquals(None, m.mentorId)
    })

    val mentor: Mentor = new Mentor(timestamp, email2, name, note, approvedTermsAndConditions, approvedSlots)
    MongoDbRepo.saveParticipant(mentor)

    VeganMentor.process

    val mentorFromDb: Mentor = MongoDbRepo.getMentorByEmail(email2).get
    assertEquals(approvedSlots, mentorFromDb.approvedSlots)
    assertEquals(approvedSlots - 1, mentorFromDb.emptySlots)

    val updateMentee: Mentee = Mentee(
      mentee.id,
      mentee.timestamp,
      mentee.email,
      mentee.name,
      mentee.note,
      mentee.approvedTermsAndConditions,
      mentorFromDb.id)
    MongoDbRepo.updateParticipant(updateMentee)
    MongoDbRepo.getMenteeByEmail("test@test.com").foreach(m => {
      assertNotEquals(None, m.id)
      assertEquals(timestamp, m.timestamp)
      assertEquals(email, m.email)
      assertEquals(name, m.name)
      assertEquals(note, m.note)
      assertTrue(approvedTermsAndConditions)
      assertEquals(mentorFromDb.id, m.mentorId)
    })
  }

  @Test
  def testProcessingTwoFiles(): Unit = {
    val testFile4 = "src/test/files/Vegan_mentor_test_04.csv"
    VeganMentor.saveInputFileToDb(testFile4)
    VeganMentor.process
    assertEquals(3, MongoDbRepo.getAllMentors.size)
    assertEquals(5, MongoDbRepo.getAllMentees.size)

    // Process another file
    val testFile5 = "src/test/files/Vegan_mentor_test_05.csv"
    VeganMentor.saveInputFileToDb(testFile5)
    VeganMentor.process
    assertEquals(3, MongoDbRepo.getAllMentors.size)
    assertEquals(7, MongoDbRepo.getAllMentees.size)
    assertEquals(1, MongoDbRepo.getMenteesSeekingMentor.size)
  }

  @Test
  def testProcessingSameFileTwice(): Unit = {
    val testFile4 = "src/test/files/Vegan_mentor_test_04.csv"
    VeganMentor.saveInputFileToDb(testFile4)
    VeganMentor.process
    assertEquals(3, MongoDbRepo.getAllMentors.size)
    assertEquals(5, MongoDbRepo.getAllMentees.size)

    // Process another file
    val testFile5 = "src/test/files/Vegan_mentor_test_04.csv"
    VeganMentor.saveInputFileToDb(testFile5)
    VeganMentor.process
    assertEquals(3, MongoDbRepo.getAllMentors.size)
    assertEquals(5, MongoDbRepo.getAllMentees.size)
  }
}
