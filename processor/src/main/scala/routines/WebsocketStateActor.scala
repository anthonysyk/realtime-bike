package routines

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import io.circe.Json
import state.StateFetcher

import scala.concurrent.{ExecutionContext, Future}

class WebsocketStateActor(val actorRef: ActorRef)(implicit val ec: ExecutionContext,
                                                  val actorSystem: ActorSystem,
                                                  val fetcher: StateFetcher) extends Actor with WebsocketProvider {

  override def fetchState: Future[Json] = fetcher.fetchAllStationsState

}

object WebsocketStateActor {

  def props(actorRef: ActorRef)(implicit ec: ExecutionContext, fetcher: StateFetcher, actorSystem: ActorSystem): Props =
    Props(new WebsocketStateActor(actorRef))

}