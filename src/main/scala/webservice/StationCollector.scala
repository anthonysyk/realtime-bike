package webservice

import akka.actor.{Actor, Props}
import config.AppConfig
import models.{Station, StationReferential}
import utils.date.DateHelper
import versatile.kafka.EmbeddedKafkaHelper
import webservice.TickActor.FetchStationsStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object StationCollector {

  val appConfig = new AppConfig

  def main(args: Array[String]): Unit ={
    if(appConfig.environment == AppConfig.Environnement.LOCAL) {
      println("Writting in embedded kafka")
      startCollectorEmbedded(appConfig)
    } else {
      println("Writting in real kafka")
      startCollector(appConfig)
    }
  }

  def startCollector(appConfig: AppConfig) = {
    val AkkaService: JCDecauxService = new JCDecauxService(appConfig)
    val tickActor = AkkaService.system.actorOf(Props[TickActor], name = "tick-actor")
    val producer: StationProducer = new StationProducer(appConfig)

    AkkaService.system.scheduler.schedule(5.seconds, 20.seconds) {
      tickActor ! FetchStationsStatus(AkkaService, producer)
    }
  }

  def startCollectorEmbedded(appConfig: AppConfig) = {
    val AkkaService: JCDecauxService = new JCDecauxService(appConfig)
    val tickActor = AkkaService.system.actorOf(Props[TickActor], name = "tick-actor")

    val embeddedKafka = new EmbeddedKafkaHelper {
      override val topics: Seq[String] = "Station" :: "Station.logs" :: Nil
    }

    embeddedKafka.startEmbeddedKafka()

    val producer = new StationProducerEmbedded(appConfig)

    AkkaService.system.scheduler.schedule(5.seconds, 1.minute) {
      tickActor ! FetchStationsStatus(AkkaService, producer)
    }
  }

}

class TickActor extends Actor {

  // TODO: make this state immutable (cats)
  // TODO: Maybe use akka persistance ?
  var currentState: Map[Int, Station] = Map.empty[Int, Station]

  val contractsFilter = Seq("Lyon", "Marseille", "Bordeaux")

  override def receive: Receive = {
    case FetchStationsStatus(service, producer) => service.getStationsList.andThen { case response =>
      val maybeResponse = response.toOption.flatMap(_.right.toOption)
      val stations: Seq[Station] = maybeResponse.map(Station.fromStationListJson).getOrElse(Nil).flatMap(_.right.toOption)
      println(s"[${DateHelper.nowReadable}] ${stations.length} stations récupérées")
      val differentStations = stations.filter(station => currentState.get(station.number) match {
        case Some(stateStation) => stateStation.equals(station)
        case None => true
      }).filter(station => contractsFilter.contains(station.contract_name))

      println(s"[${DateHelper.nowReadable}] ${differentStations.length} stations différentes")

      if (currentState.isEmpty) StationReferential.updateReferentials(stations)

      // Producer write into topic
      producer.sendStationsStatus(differentStations)
      for {
        s <- differentStations
      } yield {
        currentState += (s.number -> s)
      }
    }
  }
}

object TickActor {

  sealed trait Message

  case class FetchStationsStatus[T <: StationProducerTrait](service: JCDecauxService, producer: T ) extends Message

}
