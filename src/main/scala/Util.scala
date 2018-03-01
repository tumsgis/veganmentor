import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Date

object Util {

  private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a z")
  private val formatterWithSimplerHourFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd h:mm:ss a z")

  private def getDateFormatter(dateInput: String) =
    if (dateInput.split(" ")(1).split(":")(0).toInt < 10) formatterWithSimplerHourFormat
    else formatter

  def parseDate(dateInput: String): LocalDateTime = LocalDateTime.parse(dateInput, getDateFormatter(dateInput))

  def toJavaDate(ldt: LocalDateTime): Date =
    Date.from(ldt.atZone(ZoneId.systemDefault).toInstant)

  def fromJavaDate(javaDate: java.util.Date): LocalDateTime =
    javaDate.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime
}