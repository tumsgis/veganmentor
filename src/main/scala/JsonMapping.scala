import DataStructure.{Mentee, Mentor}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

object JsonMapping {

  implicit val formats = net.liftweb.json.DefaultFormats

  def printMentor(mentor: Mentor): Unit = {
    val json = "mentor" ->
      ("timestamp" -> mentor.timestamp.toString) ~
        ("email" -> mentor.email) ~
        ("name" -> mentor.name) ~
        ("note" -> mentor.note) ~
        ("approvedTermsAndConditions" -> mentor.approvedTermsAndConditions) ~
        ("approvedSlots" -> mentor.approvedSlots) ~
        ("emptySlots" -> mentor.emptySlots) ~
        ("mentees" ->
          mentor.mentees.map { m =>
            ("timestamp" -> m.timestamp.toString) ~
              ("email" -> m.email) ~
              ("name" -> m.name) ~
              ("note" -> m.note) ~
              ("approvedTermsAndConditions" -> m.approvedTermsAndConditions)
          })
    println(prettyRender(json))
  }


  def printMentee(mentee: Mentee): Unit = {
    val json = "mentor" ->
      ("timestamp" -> mentee.timestamp.toString) ~
        ("email" -> mentee.email) ~
        ("name" -> mentee.name) ~
        ("note" -> mentee.note) ~
        ("approvedTermsAndConditions" -> mentee.approvedTermsAndConditions) ~
        ("mentor" ->
          mentee.mentor.map { m =>
            ("timestamp" -> m.timestamp.toString) ~
              ("email" -> m.email) ~
              ("name" -> m.name) ~
              ("note" -> m.note) ~
              ("approvedTermsAndConditions" -> m.approvedTermsAndConditions) ~
              ("nrOfAvailableParticipants" -> m.approvedSlots)
          })
    println(prettyRender(json))
  }



}
