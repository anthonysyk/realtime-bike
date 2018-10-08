package state

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher
import enums.WindowInterval
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import models.{Station, StationReferential}
import utils.date.{ChartDateHelper, DateHelper}
import versatile.utils.CirceHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


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

  def fetchAllStationsReferential =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.flatMap {
        case (_, value) => parse(value).getRight.as[Station].right.toOption.map(station =>
          StationReferential(station.externalId, station.number, station.contract_name)
        ).toSeq
      }.asJson
    )

  def fetchWindow(hostKey: String, window: String) = {
    val elements = kvf.fetchWindowed(hostKey, WindowInterval.createNamespace(window), WindowInterval.createPath(window), fromTime, toTime).map(results =>
      results.distinct.flatMap(value => parse(value._2).getRight.as[Station].right.toOption.toSeq)
        .sortBy(_.last_update)
        .foldLeft(Seq.empty[Station]) { (acc, right) =>
          if (acc.lastOption.exists(_.last_update == right.last_update)) acc else acc :+ right
        })

    val labels = elements.map(elem => ChartDateHelper.createLabel(elem.map(_.last_update))._2)

    for {
      elems <- elements
      labs <- labels
    } yield elems.zip(labs).zipWithIndex.map {
      case ((station, label), index) => station.asJsonObject
        .add("index", Json.fromInt(index))
        .add("label", Json.fromString(label))
    }.asJson


  }

}
