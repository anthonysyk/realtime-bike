package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import versatile.utils.CirceHelper._
import scala.concurrent.ExecutionContext.Implicits.global


class StationStateFetcher(kvf: KeyValueFetcher[String, String]) {

  def fetchStationsStateByKey(hostKey: String) =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)
      .map(result => parse(result).getRight)


  def fetchAllAccessCountSummary =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )


  def fetchWindow3h(hostKey: String, fromTime: Long, toTime: Long) =
    kvf.fetchWindowed(hostKey, StationStateProcessor.WINDOW_STATION_STATE_3H, "/stations/access/win/3h", fromTime, toTime).map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchWindow1h(hostKey: String, fromTime: Long, toTime: Long) =
    kvf.fetchWindowed(hostKey, StationStateProcessor.WINDOW_STATION_STATE_1H, "/stations/access/win/1h", fromTime, toTime).map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchWindow1min(hostKey: String, fromTime: Long, toTime: Long) =
    kvf.fetchWindowed(hostKey, StationStateProcessor.WINDOW_STATION_STATE_1min, "/stations/access/win/1min", fromTime, toTime).map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchWindow5min(hostKey: String, fromTime: Long, toTime: Long) =
    kvf.fetchWindowed(hostKey, StationStateProcessor.WINDOW_STATION_STATE_5min, "/stations/access/win/5min", fromTime, toTime).map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchWindow30min(hostKey: String, fromTime: Long, toTime: Long) =
    kvf.fetchWindowed(hostKey, StationStateProcessor.WINDOW_STATION_STATE_30min, "/stations/access/win/30min", fromTime, toTime).map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )


}
