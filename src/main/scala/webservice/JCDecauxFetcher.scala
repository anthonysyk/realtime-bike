package webservice

import akka.actor.{Actor, Props}
import webservice.TickActor.FetchStationsStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object JCDecauxFetcher {

  def main(args: Array[String]): Unit = {

    val AkkaService: JCDecauxService = new JCDecauxService()

    val tickActor = AkkaService.system.actorOf(Props[TickActor], name = "tick-actor")

    AkkaService.system.scheduler.schedule(5.seconds, 1.minute) {

      tickActor ! FetchStationsStatus(AkkaService)

    }

  }

}

class TickActor extends Actor {
  override def receive: Receive = {
    case FetchStationsStatus(service) => service.getStationsList.andThen { case response =>
      println(response)
    }
  }
}

object TickActor {

  sealed trait Message

  case class FetchStationsStatus(service: JCDecauxService) extends Message

}
