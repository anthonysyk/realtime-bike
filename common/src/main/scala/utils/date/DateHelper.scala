package utils.date

import java.text.SimpleDateFormat
import java.util.Date

object DateHelper {

  val sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
  sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+2"))

  def nowTimestamp: Long = (new Date).getTime

  def nowReadable: String = sdf.format(new Date)

  def convertToReadable(ts: Long) = {
    sdf.format(ts)
  }
}
