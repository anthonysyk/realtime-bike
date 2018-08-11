import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.reactivestreams.Publisher
import state.StateConsumer

class ScalaConsumerExample {

  implicit val system = ActorSystem("reactive-kafka-records")
  implicit val materializer = ActorMaterializer()

  //  val example = new StateConsumer("localhost:9092", "1", "Station")
  //
  //  example.feedQueue()

  val (ref: ActorRef, publisher: Publisher[String]) = Source.actorRef[String](bufferSize = 5000, OverflowStrategy.fail)
    .toMat(Sink.asPublisher(true))(Keep.both).run()

  val example = new StateConsumer("localhost:9092", "1", "Station", ref, publisher)

  example.run()

  val source = Source.fromPublisher(publisher)

  source.runForeach(println)

}
