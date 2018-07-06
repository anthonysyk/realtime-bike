package state

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import config.AppConfig
import org.apache.kafka.streams.state.HostInfo

class StationStateWorkflow extends LazyLogging {

  def workflow() = {

    // setup REST endpoints
    val restEndpointPort = 9000
    val restEndpointHostName = "localhost"
    val restEndpoint = new HostInfo(restEndpointHostName, restEndpointPort)

    logger.info("Connecting to Kafka cluster via bootstrap servers " + AppConfig.brokers)
    logger.warn("REST endpoint at http://" + restEndpointHostName + ":" + restEndpointPort)
    println("REST endpoint at http://" + restEndpointHostName + ":" + restEndpointPort)

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

  }

}
