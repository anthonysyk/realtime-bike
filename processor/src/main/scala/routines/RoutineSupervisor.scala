package routines

import akka.actor.{ActorRef, ActorSystem, Props}
import state.StateFetcher
import versatile.memory.RuntimeMemoryActor

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


// We need 1 websocket and one websocket provider for each type of data (window, interval, etc ...)
class RoutineSupervisor(implicit fetcher: StateFetcher, ec: ExecutionContext, actorSystem: ActorSystem) {

  val runtimeActor: ActorRef = actorSystem.actorOf(Props(new RuntimeMemoryActor))

  def run(): Unit = {
    actorSystem.scheduler.schedule(
      1.minute,
      5.minute,
      runtimeActor,
      RuntimeMemoryActor.MemoryTick
    )
  }

}
