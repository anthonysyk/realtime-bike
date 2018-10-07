package websocket

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import state.StateFetcher

import scala.concurrent.ExecutionContext

class StateSocket(
                   val fetcher: StateFetcher,
                   implicit val actorSystem: ActorSystem,
                   implicit val actorMaterializer: ActorMaterializer,
                   implicit val ec: ExecutionContext
                 ) extends Socket