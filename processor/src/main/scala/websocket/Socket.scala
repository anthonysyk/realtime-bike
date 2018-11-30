package websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import config.AppConfig
import org.reactivestreams.Publisher
import state.StateFetcher

import scala.concurrent.ExecutionContext

trait Socket {

  implicit val actorMaterializer: ActorMaterializer
  implicit val actorSystem: ActorSystem
  implicit val ec: ExecutionContext
  val fetcher: StateFetcher

  val config = AppConfig.conf
  val MAX_MESSAGES = 1500

  //  https://markatta.com/codemonkey/blog/2016/04/18/chat-with-akka-http-websockets/
  val (websocketActor: ActorRef, publisher: Publisher[String]) = Source.actorRef[String](bufferSize = 2000, OverflowStrategy.dropBuffer)
    .toMat(Sink.asPublisher(true))(Keep.both).run()

  val finalSource: Source[TextMessage.Strict, NotUsed] = Source.fromPublisher(publisher).limit(MAX_MESSAGES).map(record => TextMessage(record))

  def start: Flow[Message, TextMessage, NotUsed] = {
    println("Starting websocket flow")

    val sink = Flow[Message].map {
      case TextMessage.Strict(message) => println(message)
      case _ => ()
    }.to(Sink.ignore)

    Flow.fromSinkAndSource(sink, finalSource)
  }

  def ensurePublisherIsAlive() = finalSource.to(Sink.ignore).run

}

object RunWithPublisher {
  def source[A, M](normal: Source[A, M])(implicit fm: Materializer, system: ActorSystem): (Source[A, NotUsed], M) = {
    val (normalMat, publisher) = normal.toMat(Sink.asPublisher(fanout = true))(Keep.both).run
    (Source.fromPublisher(publisher), normalMat)
  }
}
