package state

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.lightbend.kafka.scala.iq.http.InteractiveQueryHttpService
import com.typesafe.scalalogging.LazyLogging
import config.AppConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.HostInfo

trait InteractiveQueryWorkflow extends LazyLogging {

  val config = new AppConfig

  def startRestProxy(streams: KafkaStreams, hostInfo: HostInfo,
                     actorSystem: ActorSystem, materializer: ActorMaterializer): InteractiveQueryHttpService

  def createStreams(): KafkaStreams

  def workflow() = {

    // setup REST endpoints
    val restEndpointPort = 9000
    val restEndpointHostName = "localhost"
    val restEndpoint = new HostInfo(restEndpointHostName, restEndpointPort)

    logger.info("Connecting to Kafka cluster via bootstrap servers " + config.brokers)
    logger.warn("REST endpoint at http://" + restEndpointHostName + ":" + restEndpointPort)
    println("REST endpoint at http://" + restEndpointHostName + ":" + restEndpointPort)

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    // set up the topology
    val streams: KafkaStreams = createStreams()

    // Start the Restful proxy for servicing remote access to state stores
    startRestProxy(streams, restEndpoint, system, materializer)

    streams.start()

  }

}
