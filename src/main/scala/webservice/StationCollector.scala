package webservice

import akka.actor.{Actor, Props}
import models.{Station, StationReferential}
import utils.date.DateHelper
import webservice.TickActor.FetchStationsStatus
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object StationCollector {

  def main(args: Array[String]): Unit = {

    val AkkaService: JCDecauxService = new JCDecauxService()
    val tickActor = AkkaService.system.actorOf(Props[TickActor], name = "tick-actor")
    val producer: StationProducer = new StationProducer

    AkkaService.system.scheduler.schedule(5.seconds, 1.minute) {
      tickActor ! FetchStationsStatus(AkkaService, producer)
    }

  }
}

class TickActor extends Actor {

  // TODO: make this state immutable (cats)
  // TODO: Maybe use akka persistance ?
  var currentState: Map[Int, Station] = Map.empty[Int, Station]

  override def receive: Receive = {
    case FetchStationsStatus(service, producer) => service.getStationsList.andThen { case response =>
      val maybeResponse = response.toOption.flatMap(_.right.toOption)
      val stations: Seq[Station] = maybeResponse.map(Station.fromStationListJson).getOrElse(Nil).flatMap(_.right.toOption)
      println(s"[${DateHelper.nowReadable}] ${stations.length} stations récupérées")
      val differentStations = stations.filter(station => currentState.get(station.number) match {
        case Some(stateStation) => stateStation.equals(station)
        case None => true
      })
      println(s"[${DateHelper.nowReadable}] ${differentStations.length} stations différentes")

      if(currentState.isEmpty)  StationReferential.updateReferentials(stations)

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

  case class FetchStationsStatus(service: JCDecauxService, producer: StationProducer) extends Message

}
