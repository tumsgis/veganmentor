import org.mongodb.scala._

object MongoDb extends App {
  // To directly connect to the default server localhost on port 27017
  val mongoClient: MongoClient = MongoClient()

  // Use a Connection String
  //val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  // or provide custom MongoClientSettings
  //val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(List(new ServerAddress("localhost")).asJava).build()
  //val settings: MongoClientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).build()
  //val mongoClient: MongoClienst = MongoClient(settings)

  val database: MongoDatabase = mongoClient.getDatabase("mydb")

  val collection: MongoCollection[Document] = database.getCollection("test")

  val doc: Document = Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
    "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

  collection.insertOne(doc)
}