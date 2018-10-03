package http

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.lightbend.kafka.scala.iq.http.InteractiveQueryHttpService
import org.apache.kafka.streams.state.HostInfo
import org.reactivestreams.Publisher
import state.{StateConsumer, StationStateFetcher}

import scala.concurrent.ExecutionContext

class MyHTTPService(
                     hostInfo: HostInfo,
                     fetcher: StationStateFetcher,
                     actorSystem: ActorSystem,
                     actorMaterializer: ActorMaterializer,
                     ec: ExecutionContext
                   ) extends InteractiveQueryHttpService(hostInfo, actorSystem, actorMaterializer, ec) with Directives {

  //TODO: Terminer

  //  https://markatta.com/codemonkey/blog/2016/04/18/chat-with-akka-http-websockets/
  val (ref: ActorRef, publisher: Publisher[String]) = Source.actorRef[String](bufferSize = 1000000, OverflowStrategy.fail)
    .toMat(Sink.asPublisher(true))(Keep.both).run()

  val consumer = new StateConsumer("localhost:9092", "1", "State", ref, publisher)

  consumer.run()

  val source = Source.fromPublisher(publisher)

  val sourceWS = source.map(record => TextMessage(record))

  val allStationStateFlow: Flow[Message, TextMessage, NotUsed] = {
    val sink = Flow[Message].map {
      case TextMessage.Strict(message) => println(message)
      case _ => ()
    }.to(Sink.ignore)
    Flow.fromSinkAndSource(sink, sourceWS)
  }

  // define the routes
  val routes: Flow[HttpRequest, HttpResponse, Any] = {
    get {
      pathSingleSlash {
        handleWebSocketMessages(allStationStateFlow)
      }
    } ~
      pathPrefix("station") {
        (get & pathPrefix("access") & path(Segment)) {
          case "ALL" =>
            complete {
              fetcher.fetchAllAccessCountSummary
            }
          case hostKey@_ =>
            complete {
              fetcher.fetchStationsStateByKey(hostKey)
            }
        } ~
          (get & pathPrefix("access" / "win" / Segment / Segment / LongNumber / LongNumber)) { (window, hostKey, fromTime, toTime) =>
            window match {
              case "3h" =>
                complete {
                  fetcher.fetchWindow3h(hostKey, fromTime, toTime)
                }
              case "1h" =>
                complete {
                  fetcher.fetchWindow1h(hostKey, fromTime, toTime)
                }
              case "5min" =>
                complete {
                  fetcher.fetchWindow1min(hostKey, fromTime, toTime)
                }
              case "15min" =>
                complete {
                  fetcher.fetchWindow5min(hostKey, fromTime, toTime)
                }
              case "30min" =>
                complete {
                  fetcher.fetchWindow30min(hostKey, fromTime, toTime)
                }
            }
          }
      }
  }
}