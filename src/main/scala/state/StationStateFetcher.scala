package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher

class StationStateFetcher(kvf: KeyValueFetcher[String, String]) {

  def fetchWindowStationsStateByKey(hostKey: String) =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)

  def fetchAllAccessCountSummary =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL")

}
