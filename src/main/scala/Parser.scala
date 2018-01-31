import java.io.File

import DataStructure.{Mentee, Mentor, ParsedInput, Participant}
import com.github.tototoshi.csv.CSVReader

object Parser {
  def parseInputFile(filePath: String): ParsedInput = {

    val reader = CSVReader.open(new File (filePath))

    val participants: Seq[Participant] = reader.all.tail.map (o =>
      if (DataStructure.mentorShipMap (o (3) ) )
        Mentor (Util.parseDate (o.head),
          o (1),
          o (2),
          o (5),
          o (6).equals (DataStructure.termsAndConditionsApprovement),
          o (4).toInt)
      else
        Mentee (Util.parseDate (o.head),
          o (1),
          o (2),
          o (5),
          o (6).equals (DataStructure.termsAndConditionsApprovement) )
    )


    val (mentorsUnordered: Seq[Mentor], menteesUnOrdered: Seq[Mentee] ) = participants partition {_.isInstanceOf[Mentor]}

    // Erasing duplicates and then sorting.
    val mentorsOrdered = mentorsUnordered.groupBy (_.email).map (m => m._2.head).toList.sortWith ((m1, m2) => m1.timestamp.isBefore (m2.timestamp) )
    val menteesOrdered = menteesUnOrdered.groupBy (_.email).map (m => m._2.head).toList.sortWith ((m1, m2) => m1.timestamp.isBefore (m2.timestamp) )

    ParsedInput (mentorsOrdered, menteesOrdered)
  }
}
