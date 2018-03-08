
import DataStructure.{Mentee, Mentor, Participant}
import MongoDb.MongoFactory
import com.mongodb.casbah.Imports.{ObjectId, _}
import com.mongodb.casbah.{Imports, MongoCollection, MongoConnection}

object MongoDb {

  sealed trait MongoDbParticipant {
    val id: ObjectId
    val participant: Participant
  }
  case class MongoDbMentor(id: ObjectId,
                           participant: Participant,
                           emptySlots: Int) extends MongoDbParticipant
  case class MongoDbMentee(id: ObjectId,
                           participant: Participant,
                           menteeId: Option[ObjectId]) extends MongoDbParticipant

  case class MongoFactory(collectionName: String) {
    private val server = "localhost"
    private val port   = 27017
    private val db = "veganMentor"
    val connection: MongoConnection = MongoConnection(server)
    val collection: MongoCollection = connection(db)(collectionName)
  }
}

object MongoDbRepo {
  def fromUnpairedParticipant(participant: Participant,
                              relationId: Option[ObjectId] = None): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    participant match {
      case Mentor(id, _, _, _, _, _, _, _, _) =>
        builder += "_id" -> id
      case _ => /* Nothing */
    }
    builder += "timestamp" -> Util.toJavaDate(participant.timestamp)
    builder += "email" -> participant.email
    builder += "name" -> participant.name
    builder += "note" -> participant.note
    builder += "approvedTermsAndConditions" -> participant.approvedTermsAndConditions
    (participant, relationId) match {
      // Add mentor id to mentees (kind of a foreign key)
      case (Mentee(_, _, _, _, _, _, _, _), Some(id)) => builder += "mentorId" -> id
      case (Mentor(_, _, _, _, _, _, approvedSlots, _, mentees), _) =>
        builder += "approvedSlots" -> approvedSlots
        builder += "emptySlots" -> (approvedSlots - mentees.size)
      case _ => /* Nothing */
    }
    builder.result
  }

  def saveParticipant(participant: Participant): Unit = {
    val mongoObj = fromUnpairedParticipant(participant)
    participant match {
      case Mentor(_, _, _, _, _, _, _, _, mentees) => saveParticipant(mongoObj, "mentor", participant.email)
        // Add all connected mentors
        mentees.foreach(m => {
          val menteeObj = fromUnpairedParticipant(m, mongoObj._id)
          saveParticipant(menteeObj, "mentee", m.email)
        })
      case Mentee(_, _, _, _, _, _, _, _) => saveParticipant(mongoObj, "mentee", participant.email)
    }
  }

  private def saveParticipant(mongoObj: MongoDBObject, collection: String, email: String): Unit =
    if (!emailAlreadyRegistered(email))
      MongoFactory(collection).collection.save(mongoObj, WriteConcern.Safe)

  private def emailAlreadyRegistered(email: String): Boolean = {
    if (getMentorByEmail(email).nonEmpty) {
      println(s"Mentor with email $email already registered")
      return true
    } else if (getMenteeByEmail(email).nonEmpty) {
      println(s"Mentee with email $email already registered")
      return true
    }
    false
  }

  def updateParticipant(participant: Participant): Unit = {
    val mongoObj = fromUnpairedParticipant(participant)
    participant match {
      case Mentor(id, _, _, _, _, _, _, emptySlots, mentees) => updateParticipant("mentor", id, ("emptySlots", emptySlots - mentees.size))
        // Add all connected mentors
        mentees.foreach(m => {
          val menteeObj = fromUnpairedParticipant(m, mongoObj._id)
          updateParticipant("mentee", m.id, ("mentorId", mongoObj._id.get))
        })
      case _ => /* Nothing */
    }
  }

  private def updateParticipant(collection: String, id: Option[Imports.ObjectId], field: (String, Any)): Unit =
    MongoFactory(collection).collection.update(MongoDBObject("_id" -> id), $set(field))

  def toMentor(dbObject: DBObject): Mentor =
    Mentor(
      Some(dbObject.as[ObjectId]("_id")),
      Util.fromJavaDate(dbObject.as[java.util.Date]("timestamp")),
      dbObject.as[String]("email"),
      dbObject.as[String]("name"),
      dbObject.as[String]("note"),
      dbObject.as[Boolean]("approvedTermsAndConditions"),
      dbObject.as[Int]("approvedSlots"),
      dbObject.as[Int]("emptySlots")
    )

  def toMentee(dbObject: DBObject): Mentee =
    Mentee(
      Some(dbObject.as[ObjectId]("_id")),
      Util.fromJavaDate(dbObject.as[java.util.Date]("timestamp")),
      dbObject.as[String]("email"),
      dbObject.as[String]("name"),
      dbObject.as[String]("note"),
      dbObject.as[Boolean]("approvedTermsAndConditions"),
      if (dbObject.containsField("mentorId")) Some(dbObject.as[ObjectId]("mentorId")) else None,
      None
    )

  def getAllMentors: List[Mentor] =
    MongoFactory("mentor").collection.find.map(toMentor).toList

  def getAllMentees: List[Mentee] =
    MongoFactory("mentee").collection.find.map(toMentee).toList

  def get(collection: String, query: DBObject): Iterator[Imports.DBObject] =
    for (x <- MongoFactory(collection).collection.find(query)) yield x

  def getOne(collection: String, query: DBObject): Option[Imports.DBObject] =
    for (x <- MongoFactory(collection).collection.findOne(query)) yield x

  def getMentorsWithEmptySlots: List[Mentor] =
    get("mentor", $and("approvedTermsAndConditions" $eq true, "emptySlots" $gt 0)).map(toMentor).toList

  def getMentorByEmail(email: String): Option[Mentor] =
    getOne("mentor", "email" $eq email).map(toMentor)

  def getMenteesSeekingMentor: List[Mentee] =
    get("mentee", $and("approvedTermsAndConditions" $eq true, "mentorId" $exists false)).map(toMentee).toList

  def getMenteeByEmail(email: String): Option[Mentee] =
    getOne("mentee", "email" $eq email).map(toMentee)

  def getMenteesByMentorId(mentorId: ObjectId): List[Mentee] =
    get("mentee", "mentorId" $eq mentorId).map(toMentee).toList

  def dropAllParticipants(): Unit = {
    MongoFactory("mentor").collection.drop
    MongoFactory("mentee").collection.drop
  }
}