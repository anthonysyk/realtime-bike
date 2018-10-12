package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.lightbend.kafka.scala.iq.http.InteractiveQueryHttpService
import enums.WindowInterval
import models.StationReferential
import org.apache.kafka.streams.state.HostInfo
import routines.RoutineSupervisor
import state.StateFetcher
import utils.PathHelper
import websocket.StateSocket

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.{Failure, Success}

class MyHTTPService(
                     hostInfo: HostInfo,
                     actorSystem: ActorSystem,
                     actorMaterializer: ActorMaterializer,
                     ec: ExecutionContext,
                     implicit val fetcher: StateFetcher
                   ) extends InteractiveQueryHttpService(hostInfo, actorSystem, actorMaterializer, ec) with Directives {


  val StateSocket = new StateSocket(fetcher, actorSystem, actorMaterializer, ec)

  val routineSupervisor = new RoutineSupervisor(StateSocket.websocketActor)

  routineSupervisor.run()

  // start the http server
  override def start(): Unit = {
    bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", hostInfo.port)

    bindingFuture.onComplete {
      case Success(serverBinding) =>
        logger.info(s"Server bound to ${serverBinding.localAddress} ")

      case Failure(ex) =>
        logger.error(s"Failed to bind to ${hostInfo.host}:${hostInfo.port}!", ex)
        actorSystem.terminate()
    }
  }

  // define the routes
  val routes: Flow[HttpRequest, HttpResponse, Any] = {
    get {
      pathSingleSlash {
        StateSocket.ensurePublisherIsAlive()
        handleWebSocketMessages(StateSocket.start())
      }
    } ~
      path("stations") {
        complete(fetcher.fetchAllStationsReferential)
      } ~
      path("station" / "access" / Segment) {
        case "ALL" =>
          complete {
            fetcher.fetchAllStationsState
          }
        case contract@_ => complete {
          fetcher.fetchStationsStateByContract(contract)
        }
      } ~
      pathPrefix("station") {
        (get & pathPrefix("access") & path(Segment)) {
          case hostKey@_ =>
            complete {
              fetcher.fetchStationsStateByKey(hostKey)
            }
        } ~
          (get & pathPrefix("access" / "win" / Segment / Segment)) { (window, hostKey) =>
            if (WindowInterval.validateInterval(window)) {
              complete {
                fetcher.fetchWindow(hostKey, window)
              }
            } else complete(StatusCodes.NotAcceptable, "not a valid window interval")
          }
      }
  }
}