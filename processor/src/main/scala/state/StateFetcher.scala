package state

import enums.WindowInterval
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser.parse
import io.circe.syntax._
import models._
import utils.date.{ChartDateHelper, DateHelper}
import versatile.json.CirceHelper._
import versatile.kafka.iq.http.KeyValueFetcher

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class StateFetcher(kvf: KeyValueFetcher[String, String]) {

  def fetchStationsStateByKey(hostKey: String): Future[Json] =
    kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)
      .map(result => parse(result).getRight)


  def fetchAllStationsState: Future[Json] =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.map {
        case (key, value) => parse(value).getRight.asObject.map(_.add("id", key.asJson))
      }.asJson
    )

  def fetchStationsStateByContract(contract: String): Future[Json] =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.map {
        case (key, value) => parse(value).getRight.as[Station].right.toOption
      }.filter(_.exists(_.contract_name == contract)).flatMap(_.toSeq).asJson
    )

  def fetchAllStationsReferential: Future[Json] =
    kvf.fetchAll(StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/ALL").map(results =>
      results.flatMap {
        case (_, value) => parse(value).getRight.as[Station].right.toOption.map(station =>
          StationReferential(station.externalId, station.number, station.contract_name, station.address)
        ).toSeq
      }.asJson
    )

  def fetchWindow(hostKey: String, window: String): Future[Json] = {
    val toTime = DateHelper.tomorrowTimestamp
    val fromTime = DateHelper.oldestTimestamp
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

  def fetchStationsStateByKeyWithCoordinates(hostKey: String, coordinates: Coordinates): Future[Seq[Station]] = {
    for {
      stations <- kvf.fetch(hostKey, StationStateProcessor.ACCESS_STATION_STATE, "/stations/access/" + hostKey)
        .map(result => parse(result).getRight.as[Station].right.toOption.toSeq)
      stationsInsideMap = stations.filter { station =>
        val stationX = station.position.lat
        val stationY = station.position.lng
        val isXInside = stationX > coordinates.topLeft && stationX < coordinates.bottomRight
        val isYInside = stationY > coordinates.bottomLeft && stationY < coordinates.topRight
        isXInside && isYInside
      }
    } yield stationsInsideMap
  }


  def fetchTopStations: Future[Map[String, List[TopStation]]] = {
    val toTime = DateHelper.tomorrowTimestamp
    val fromTime = DateHelper.oldestTimestamp
    val elements = kvf.fetchAllWindowed(StationStateProcessor.TOP_STATION_STATE, "/station/top/ALL", fromTime, toTime).map(results =>
      results.distinct.flatMap(value => parse(value._2).getRight.as[TopStation].right.toOption.toSeq)
    )

    for {
      topStations <- elements
    } yield {
      topStations.groupBy(_.contract_name).mapValues(_.sortWith((a, b) => (a.bikes_taken + a.bikes_droped) > (b.bikes_taken + b.bikes_droped)).take(5))
    }
  }

  def fetchCitiesTotalDelta: Future[Seq[TopCity]] = {
    val toTime = DateHelper.tomorrowTimestamp
    val fromTime = DateHelper.oldestTimestamp
    val elements = kvf.fetchAllWindowed(StationStateProcessor.TOP_STATION_STATE, "/station/top/ALL", fromTime, toTime).map(results =>
      results.distinct.flatMap(value => parse(value._2).getRight.as[TopStation].right.toOption.toSeq)
    )

    for {
      topStations <- elements
    } yield {
      topStations.groupBy(_.contract_name).map { case (city, stations) =>
        val lastStationsWindow = stations.groupBy(_.number).mapValues(_.sortBy(_.start_timestamp).reverse.headOption).values
        lastStationsWindow.flatten.foldLeft(TopCity(city, 0, 0))((a, b) =>
          TopCity(
            name = city,
            bikes_taken = a.bikes_taken + b.bikes_taken,
            bikes_droped = a.bikes_droped + b.bikes_droped
          )
        )
      }.toSeq
    }
  }

}
