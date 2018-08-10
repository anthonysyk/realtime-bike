package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import versatile.utils.CirceHelper._
import scala.concurrent.ExecutionContext.Implicits.global


class StationStateFetcher(kvf: KeyValueFetcher[String, String]) {

  def fetchWindowStationsStateByKey(hostKey: String) =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey).map(_.asJson)

  def fetchAllAccessCountSummary =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

}
