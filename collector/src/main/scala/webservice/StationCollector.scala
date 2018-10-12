package webservice

import akka.actor.{Actor, Props}
import config.AppConfig
import models.{ParisStation, Station, SummaryParisStation}
import utils.date.DateHelper
import versatile.kafka.EmbeddedKafkaHelper
import webservice.TickActor.{FetchStationsStatus, FetchParisStationStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object StationCollector {

  val appConfig = AppConfig.conf

  def main(args: Array[String]): Unit = {
    println("Writting in real kafka")
    startCollector(appConfig)
  }

  def startCollector(appConfig: AppConfig) = {
    val akkaService: BikeApiService = new BikeApiService(appConfig)
    val tickActor = akkaService.system.actorOf(Props[TickActor], name = "tick-actor")
    val producer: StationProducer = new StationProducer(appConfig)

    akkaService.system.scheduler.schedule(5.seconds, 60.seconds) {
      tickActor ! FetchStationsStatus(akkaService, producer)
      tickActor ! FetchParisStationStatus(akkaService, producer)
    }
  }

  def startCollectorEmbedded(appConfig: AppConfig) = {
    val akkaService: BikeApiService = new BikeApiService(appConfig)
    val tickActor = akkaService.system.actorOf(Props[TickActor], name = "tick-actor")

    val embeddedKafka = new EmbeddedKafkaHelper {
      override val topics: Seq[String] = "Station" :: "Station.logs" :: Nil
    }

    embeddedKafka.startEmbeddedKafka()

    val producer = new StationProducerEmbedded(appConfig)

    akkaService.system.scheduler.schedule(5.seconds, 1.minute) {
      tickActor ! FetchStationsStatus(akkaService, producer)
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

  val contractsFilter = Seq("Lyon", "Marseille", "Bordeaux", "Lille")

  override def receive: Receive = {
    case FetchStationsStatus(service, producer) => service.getStationsList.andThen { case response =>
      val maybeResponse = response.toOption.flatMap(_.right.toOption)
      val stations: Seq[Station] = maybeResponse.map(Station.fromStationListJson).getOrElse(Nil).flatMap(_.right.toOption)
      val filteredStations = stations.filter(station => contractsFilter.contains(station.contract_name))
          .filterNot(station => currentStateJCDecaux.get(station.externalId).contains(station.last_update))

      println(s"[${DateHelper.nowReadable}] ${filteredStations.length} stations récupérées")

      // Producer write into topic
      producer.sendStationsStatus(filteredStations)

      filteredStations.foreach { s =>
        currentStateJCDecaux += (s.externalId -> s.last_update)
      }
    }
    case FetchParisStationStatus(service, producer) => service.getParisStations.andThen { case response =>
      val maybeResponse = response.toOption.flatMap(_.right.toOption)
      val stationsWithSummary = maybeResponse.map(response =>
        ParisStation.fromStationListJson(response, currentStateParis)
      ).getOrElse(Nil).flatMap(_.right.toOption).flatMap(_.toSeq)

      println(s"[${DateHelper.nowReadable}] ${stationsWithSummary.length} stations de Paris récupérées")

      // Producer write into topic
      producer.sendStationsStatus(stationsWithSummary.map(_._1))

        stationsWithSummary.foreach{
          case (_, summary) => currentStateParis += (summary.stationInfo.name -> summary)
        }
    }
  }
}

object TickActor {

  sealed trait Message

  case class FetchStationsStatus[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

  case class FetchParisStationStatus[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

}
