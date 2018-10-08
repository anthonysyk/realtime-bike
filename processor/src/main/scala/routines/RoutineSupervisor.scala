package routines

import akka.actor.{ActorRef, ActorSystem, Props}
import state.StateFetcher

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


// We need 1 websocket and one websocket provider for each type of data (window, interval, etc ...)
class RoutineSupervisor(websocketActor: ActorRef)(implicit fetcher: StateFetcher, ec: ExecutionContext, actorSystem: ActorSystem) {

  // Routines actors sending data to the websocket
  val websocketStateActor: ActorRef = actorSystem.actorOf(WebsocketStateActor.props(websocketActor), "state")
  val runtimeActor = actorSystem.actorOf(Props(new RuntimeActor))

  def run(): Unit = {
    actorSystem.scheduler.schedule(
      1.second,
      1.minute,
      websocketStateActor,
      WebsocketProvider.FetchTick
    )

    actorSystem.scheduler.schedule(
      1.second,
      10.minute,
      runtimeActor,
      RuntimeActor.MemoryTick
    )
  }

}
