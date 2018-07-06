package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher

import scala.concurrent.Future

class StationStateFetcher(kvf: KeyValueFetcher) {

  def fetchWindowStationsStateByKey(hostKey: String): Future[Long] =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)

}
