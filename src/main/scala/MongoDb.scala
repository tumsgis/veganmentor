
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
      case Mentor(_, _, _, _, _, _, _, _, mentees) =>
        MongoFactory("mentor").collection.save(mongoObj, WriteConcern.Safe)
        // Add all connected mentors
        mentees.foreach(m => {
          val menteeObj = fromUnpairedParticipant(m, mongoObj._id)
          MongoFactory("mentee").collection.save(menteeObj, WriteConcern.Safe)
        })
      case Mentee(_, _, _, _, _, _, _, _) => MongoFactory("mentee").collection.save(mongoObj, WriteConcern.Safe)
    }
  }

  def updateParticipant(participant: Participant): Unit = {
    val mongoObj = fromUnpairedParticipant(participant)
    participant match {
      case Mentor(id, _, _, _, _, _, _, emptySlots, mentees) =>
        MongoFactory("mentor").collection.update(MongoDBObject("_id" -> id), $set(("emptySlots", emptySlots - mentees.size)))
        // Add all connected mentors
        mentees.foreach(m => {
          val menteeObj = fromUnpairedParticipant(m, mongoObj._id)
          MongoFactory("mentee").collection.update(MongoDBObject("_id" -> m.id), $set(("mentorId", mongoObj._id.get)))
        })
      case _ => /* Nothing */
    }
  }

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
      if (dbObject.containsField("mentorId")) dbObject.as[Option[ObjectId]]("mentorId") else None,
      None
    )

  def getAllMentors: List[Mentor] =
    MongoFactory("mentor").collection.find.map(toMentor).toList

  def getAllMentees: List[Mentee] =
    MongoFactory("mentee").collection.find.map(toMentee).toList

  def get(collection: String, query: DBObject): Iterator[Imports.DBObject] =
    for (x <- MongoFactory(collection).collection.find(query)) yield x

  def getMentorsWithEmptySlots: List[Mentor] =
    get("mentor", $and("approvedTermsAndConditions" $eq true, "emptySlots" $gt 0)).map(toMentor).toList

  def getMentorByName(name: String): List[Mentor] =
    get("mentor", "name" $eq name).map(toMentor).toList

  def getMenteesSeekingMentor: List[Mentee] =
    get("mentee", $and("approvedTermsAndConditions" $eq true, "mentorId" $exists false)).map(toMentee).toList

  def getMenteeByName(name: String): List[Mentee] =
    get("mentee", "name" $eq name).map(toMentee).toList

  def getMenteesByMentorId(mentorId: ObjectId): List[Mentee] =
    get("mentee", "mentorId" $eq mentorId).map(toMentee).toList

  def dropAllParticipants(): Unit = {
    MongoFactory("mentor").collection.drop
    MongoFactory("mentee").collection.drop
  }
}