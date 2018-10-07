package routines

import akka.actor.{Actor, ActorRef, ActorSystem}
import io.circe.Json
import routines.WebsocketProvider._
import state.StateFetcher

import scala.concurrent.{ExecutionContext, Future}

trait WebsocketProvider extends Actor {

  val fetcher: StateFetcher
  val actorRef: ActorRef
  val actorSystem: ActorSystem
  implicit val ec: ExecutionContext

  def fetchState: Future[Json]

  def receive = {
    case FetchTick => fetchState.map { json =>
      println("sending data to websocket")
      actorRef ! json.noSpaces
    }
  }

}

object WebsocketProvider {

  case object FetchTick

}
