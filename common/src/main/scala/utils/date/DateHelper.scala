package utils.date

import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.DateTime

object DateHelper {

  val sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
  sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+2"))

  def nowTimestamp: Long = (new Date).getTime

  def nowReadable: String = sdf.format(new Date)

  def oldestTimestamp: Long = new Date(Long.MinValue).getTime

  def tomorrowTimestamp: Long = new Date(new DateTime(nowTimestamp).plusDays(1).getMillis).getTime

  def minusHoursTimestamp(hours: Int): Long = new Date(new DateTime(nowTimestamp).minusHours(hours).getMillis).getTime

  def convertToReadable(ts: Long) = {
    sdf.format(ts)
  }
}
