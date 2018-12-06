package http

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import config.AppConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.HostInfo
import versatile.kafka.iq.http.http.InteractiveQueryHttpService

trait InteractiveQueryWorkflow {

  val config = AppConfig.conf

  def startRestProxy(streams: KafkaStreams, hostInfo: HostInfo,
                     actorSystem: ActorSystem,
                     materializer: ActorMaterializer): InteractiveQueryHttpService

  def createStreams(): KafkaStreams

  def workflow() = {

    // setup REST endpoints
    val restEndpointPort = config.webservice.port
    val restEndpointHostName = config.webservice.host
    val restEndpoint = new HostInfo(restEndpointHostName, restEndpointPort)

    println("Connecting to Kafka cluster via bootstrap servers " + config.kafka.bootstrap_server)
    println("REST endpoint at http://" + restEndpointHostName + ":" + restEndpointPort)

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // set up the topology
    val streams: KafkaStreams = createStreams()

    // Start the Restful proxy for servicing remote access to state stores
    startRestProxy(streams, restEndpoint, system, materializer)

    streams.start()

  }

}
