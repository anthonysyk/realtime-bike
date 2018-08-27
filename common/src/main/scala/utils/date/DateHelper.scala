package utils.date

import java.text.SimpleDateFormat
import java.util.Date

object DateHelper {

  val formatReadable = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

  def nowTimestamp: Long = (new Date).getTime

  def nowReadable: String = formatReadable.format(new Date)

  def convertToReadable(ts: Long) = {
    formatReadable.format(ts)
  }
}
