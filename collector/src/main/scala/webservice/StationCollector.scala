package webservice

import akka.actor.{Actor, Cancellable, Props}
import config.AppConfig
import io.circe.parser._
import models.{ParisStation, Station, SummaryParisStation}
import utils.date.DateHelper
import webservice.TickActor._

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
      tickActor ! FetchJcDecauxStations(akkaService, producer)
      tickActor ! FetchParisStations(akkaService, producer)
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

  val JCDecauxAPISource = "JCDecauxAPI"
  val ParisVelibSource = "ParisVelib"

  val AllStationsEventType = "all_stations"
  val ParisEventType = "Paris"

  def jsonToArr(string: String): Seq[String] = {
    parse(string).fold({
      _.getMessage() +: Nil
    }, {
      _.asArray.getOrElse(Nil).map(_.noSpaces)
    })
  }

  override def receive: Receive = {
    case FetchJcDecauxStations(service, producer) => service.getStationsList.andThen {
      case Success(response) =>
          producer.sendRawResponseEvent(JCDecauxAPISource, response.fold(identity, identity), AllStationsEventType)
      case Failure(error) =>
        producer.sendRawResponseEvent(JCDecauxAPISource, error.getMessage, AllStationsEventType)
    }.map {
      case Right(response) =>
        val rawStations: Seq[Station] = Station.fromStationListJson(response).flatMap(_.right.toOption)

        val stationsWithoutDuplicates: Seq[Station] = rawStations
          .filterNot(station => currentStateJCDecaux.get(station.externalId).contains(station.last_update))

        val groupedStations: Map[String, Seq[Station]] = rawStations.groupBy(station => station.contract_name)

        groupedStations.foreach { case (contract, stations) =>
          println(s"[${DateHelper.nowReadable}] ${stations.length} stations récupérées pour $contract")
        }
        println(s"[${DateHelper.nowReadable}] ${stationsWithoutDuplicates.length} stations récupérées sur JcDecaux API\n")

        groupedStations.foreach { case (contract, stations) =>
          producer.sendStationEvents(stations, contract)
        }

        stationsWithoutDuplicates.foreach { station =>
          currentStateJCDecaux += (station.externalId -> station.last_update)
        }
    }

    case FetchParisStations(service, producer) => service.getParisStations.andThen {
      case Success(response) =>
        producer.sendRawResponseEvent(ParisVelibSource, response.fold(identity, identity), ParisEventType)
      case Failure(exception) =>
        producer.sendRawResponseEvent(ParisVelibSource, exception.getMessage, ParisEventType)
    }.map {
      case Right(response) =>
        val stationsWithSummary: Seq[(Station, SummaryParisStation)] =
          ParisStation.fromStationListJson(response, currentStateParis).flatMap(_.right.toOption.flatten.toSeq)

        println(s"[${DateHelper.nowReadable}] ${stationsWithSummary.length} stations de Paris récupérées")

        producer.sendStationEvents(stationsWithSummary.map(_._1), ParisEventType)

        stationsWithSummary.foreach {
          case (_, summary) => currentStateParis += (summary.stationInfo.name -> summary)
        }
    }

  }
}

object TickActor {

  sealed trait Message

  case class FetchJcDecauxStations[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

  case class FetchParisStations[T <: StationProducerTrait](service: BikeApiService, producer: T) extends Message

}
