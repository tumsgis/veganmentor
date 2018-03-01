import java.io.File

import DataStructure.{Mentee, Mentor, SortedQueue, Participant}
import com.github.tototoshi.csv.CSVReader

object Parser {
  def parseInputFile(filePath: String): (Seq[Mentor], Seq[Mentee]) = {

    val reader = CSVReader.open(new File (filePath))

    val participants: Seq[Participant] = reader.all.tail.map (o =>
      if (DataStructure.mentorShipMap (o (3) ) )
        new Mentor (Util.parseDate (o.head),
          o (1),
          o (2),
          o (5),
          o (6).trim.nonEmpty,
          o (4).toInt)
      else
        new Mentee (Util.parseDate (o.head),
          o (1),
          o (2),
          o (5),
          o (6).trim.nonEmpty)
    )


    val (mentorsUnordered: Seq[Mentor], menteesUnOrdered: Seq[Mentee] ) = participants partition {_.isInstanceOf[Mentor]}
    (mentorsUnordered, menteesUnOrdered)
  }
}
