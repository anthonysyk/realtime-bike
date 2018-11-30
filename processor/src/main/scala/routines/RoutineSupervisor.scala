package routines

import akka.actor.{ActorRef, ActorSystem, Props}
import state.StateFetcher
import versatile.memory.RuntimeMemoryActor

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


// We need 1 websocket and one websocket provider for each type of data (window, interval, etc ...)
class RoutineSupervisor(websocketActor: ActorRef)(implicit fetcher: StateFetcher, ec: ExecutionContext, actorSystem: ActorSystem) {

  // Routines actors sending data to the websocket
  val websocketStateActor: ActorRef = actorSystem.actorOf(WebsocketStateActor.props(websocketActor), "state")
  val runtimeActor = actorSystem.actorOf(Props(new RuntimeMemoryActor))

  def run(): Unit = {
    actorSystem.scheduler.schedule(
      1.minute,
      1.minute,
      websocketStateActor,
      WebsocketProvider.FetchTick
    )

    actorSystem.scheduler.schedule(
      1.minute,
      5.minute,
      runtimeActor,
      RuntimeMemoryActor.MemoryTick
    )
  }

}
