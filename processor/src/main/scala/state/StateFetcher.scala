package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher
import enums.WindowInterval
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.Station
import utils.date.DateHelper
import versatile.utils.CirceHelper._

import scala.concurrent.ExecutionContext.Implicits.global


class StateFetcher(kvf: KeyValueFetcher[String, String]) {

  val toTime = DateHelper.tomorrowTimestamp
  val fromTime = DateHelper.oldestTimestamp

  def fetchStationsStateByKey(hostKey: String) =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)
      .map(result => parse(result).getRight)


  def fetchAllStationsState =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchWindow(hostKey: String, window: String) = {
    kvf.fetchWindowed(hostKey, WindowInterval.createNamespace(window), WindowInterval.createPath(window), fromTime, toTime).map(results =>
      results.distinct.flatMap(value => parse(value._2).getRight.as[Station].right.toOption.toSeq)
      .sortBy(_.last_update)
      .foldLeft(Seq.empty[Station]) { (acc, right) =>
        if(acc.lastOption.exists(_.last_update == right.last_update)) acc else acc :+ right
      }.asJson
    )

  }

}
