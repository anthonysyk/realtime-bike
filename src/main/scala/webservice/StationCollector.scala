package webservice

import java.util.concurrent.Future

import akka.actor.{Actor, Props}
import kafka.{KafkaProducerHelper, StationProducer}
import models.Station
import org.apache.kafka.clients.producer.RecordMetadata
import utils.date.DateHelper
import webservice.TickActor.FetchStationsStatus

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
  override def receive: Receive = {
    case FetchStationsStatus(service, producer) => service.getStationsList.andThen { case response =>
      val maybeResponse = response.toOption.flatMap(_.right.toOption)
      val stations: Seq[Station] = maybeResponse.map(Station.fromStationListJson).getOrElse(Nil).flatMap(_.right.toOption)
      println(s"[${DateHelper.nowReadable}] ${stations.length} stations récupérées")
      // Producer write into topic
      producer.sendStationsStatus(stations)
    }
  }
}

object TickActor {

  sealed trait Message

  case class FetchStationsStatus(service: JCDecauxService, producer: StationProducer) extends Message

}
