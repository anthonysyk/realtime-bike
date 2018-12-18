package webservice

import akka.actor.{Actor, Cancellable, Props}
import config.AppConfig
import models.{ParisStation, Station, SummaryParisStation}
import utils.date.DateHelper
import webservice.TickActor.{FetchParisStationStatus, FetchStationsStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object StationCollector {

  val appConfig = AppConfig.conf

  def main(args: Array[String]): Unit = {
    println("Writing in kafka")
    startCollector(appConfig)
  }

  def startCollector(appConfig: AppConfig): Cancellable = {
    val akkaService: BikeApiService = new BikeApiService(appConfig)
    val tickActor = akkaService.system.actorOf(Props[TickActor], name = "tick-actor")
    val producer: StationProducer = new StationProducer(appConfig)

    akkaService.system.scheduler.schedule(5.seconds, 60.seconds) {
      tickActor ! FetchStationsStatus(akkaService, producer)
      tickActor ! FetchParisStationStatus(akkaService, producer)
    }
  }

}

class TickActor extends Actor {

  // TODO: make this state immutable (cats)
  // TODO: Maybe use akka persistance ?
  // ExternalId -> LastUpdate
  var currentStateJCDecaux: Map[String, Long] = Map.empty[String, Long]
  // StationName -> Summary
  var currentStateParis: Map[String, SummaryParisStation] = Map.empty[String, SummaryParisStation]

  val contractsFilter = Seq("Lyon", "Marseille", "Bordeaux", "Lille", "Nantes")

  val JCDecauxAPI = "JCDecauxAPI"
  val VelibParis = "VelibParis"

  val JCDecauxEventType = "fetch_station_jcdecaux"
  val VelibParisEventType = "fetch_station_velib"

  override def receive: Receive = {
    case FetchStationsStatus(service, producer) => service.getStationsList.andThen { case Success(response) =>

      producer.sendRawResponseEvent(JCDecauxAPI, JCDecauxEventType, response.fold(identity, identity))

      val maybeResponse = response.right.toOption
      val stations: Seq[Station] = maybeResponse.map(Station.fromStationListJson).getOrElse(Nil).flatMap(_.right.toOption)
      val filteredStations = stations.filter(station => contractsFilter.contains(station.contract_name))
        .filterNot(station => currentStateJCDecaux.get(station.externalId).contains(station.last_update))

      println(s"[${DateHelper.nowReadable}] ${filteredStations.length} stations récupérées")

      // Producer write into topic
      producer.sendStationEvents(filteredStations, JCDecauxEventType)

      filteredStations.foreach { s =>
        currentStateJCDecaux += (s.externalId -> s.last_update)
      }

    case Failure(exception) => producer.sendRawResponseEvent(JCDecauxAPI, JCDecauxEventType, exception.getMessage)
    }
    case FetchParisStationStatus(service, producer) => service.getParisStations.andThen {
      case Success(response) =>
        producer.sendRawResponseEvent(VelibParis, VelibParisEventType, response.fold(identity, identity))

        val maybeResponse = response.right.toOption
        val stationsWithSummary: Seq[(Station, SummaryParisStation)] = maybeResponse.map(response =>
          ParisStation.fromStationListJson(response, currentStateParis)
        ).getOrElse(Nil).flatMap(_.right.toOption).flatMap(_.toSeq)

        println(s"[${DateHelper.nowReadable}] ${stationsWithSummary.length} stations de Paris récupérées")

        // Producer write into topic
        producer.sendStationEvents(stationsWithSummary.map(_._1), VelibParisEventType)

        stationsWithSummary.foreach {
          case (_, summary) => currentStateParis += (summary.stationInfo.name -> summary)
        }
      case Failure(exception) =>
        producer.sendRawResponseEvent(VelibParis, VelibParisEventType,exception.getMessage)

    }
  }
}

object TickActor {

  sealed trait Message

  case class FetchStationsStatus[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

  case class FetchParisStationStatus[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

}
