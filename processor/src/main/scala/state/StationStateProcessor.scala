package state

import java.util.Properties
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.lightbend.kafka.scala.iq.http.{HttpRequester, KeyValueFetcher}
import com.lightbend.kafka.scala.iq.serializers.Serializers
import com.lightbend.kafka.scala.iq.services.{LocalStateStoreQuery, MetadataService}
import com.lightbend.kafka.scala.streams.{KStreamS, StreamsBuilderS}
import io.circe.parser._
import io.circe.syntax._
import models.{Serde, Station, StationState}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{Materialized, Produced, Serialized}
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.{Consumed, KafkaStreams, StreamsConfig}
import utils.PathHelper

import scala.concurrent.ExecutionContext

object StationStateProcessor extends Serializers with InteractiveQueryWorkflow {

  final val ACCESS_STATION_STATE = "access-station-state"
  final val AVAILABILITY_STATE = "availability-state"

  import versatile.utils.CirceHelper._

  def main(args: Array[String]): Unit = workflow()

  override def startRestProxy(streams: KafkaStreams, hostInfo: HostInfo,
                              actorSystem: ActorSystem, materializer: ActorMaterializer): MyHTTPService = {

    implicit val system = actorSystem

    lazy val defaultParallelism: Int = {
      val rt = Runtime.getRuntime
      rt.availableProcessors() * 4
    }

    def defaultExecutionContext(parallelism: Int = defaultParallelism): ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallelism))

    val executionContext = defaultExecutionContext()

    // service for fetching metadata information
    val metadataService = new MetadataService(streams)

    // service for fetching from local state store
    val localStateStoreQuery = new LocalStateStoreQuery[String, String]

    // http service for request handling
    val httpRequester = new HttpRequester(system, materializer, executionContext)

    implicit val ss = stringSerializer

    val restService = new MyHTTPService(
      hostInfo,
      new StationStateFetcher(
        new KeyValueFetcher[String, String](metadataService, localStateStoreQuery, httpRequester, streams, executionContext, hostInfo)
      ),
      system, materializer, executionContext
    )
    restService.start()
    restService
  }

  def createStationStateSummary(stationRecord: KStreamS[String, String])(implicit builder: StreamsBuilderS) = {

    val groupedStream = stationRecord.flatMapValues(parse(_).getRight.as[Station].right.toOption)
      .groupByKey(Serialized.`with`(stringSerde, Serde.STATION_SERDE))

    /* ACCESS_STATION_STATE */
    groupedStream
      .aggregate(
        () => Station.empty.asJson.noSpaces,
        StateAggregators.foldStationState,
        Materialized.as(ACCESS_STATION_STATE)
          .withKeySerde(stringSerde)
          .withValueSerde(stringSerde)
      ).toStream.to("State")(Produced.`with`(stringSerde, stringSerde))

  }

  override def createStreams(): KafkaStreams = {
    // Kafka stream configuration
    val streamingConfig = {
      val streamsConfiguration = new Properties()
      streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, s"state-${scala.util.Random.nextInt(100)}")
      streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "station-state")
      streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka.bootstrap_server)
      // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
      // Note: To re-run the demo, you need to use the offset reset tool:
      // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
      streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
      streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

      streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, s"localhost:${config.webservice.port}")

      // default is /tmp/kafka-streams
      streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, PathHelper.StateStoreDirectory)

      // Set the commit interval to 500ms so that any changes are flushed frequently and the summary
      // data are updated with low latency.
      streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500")

      streamsConfiguration
    }

    implicit val builder = new StreamsBuilderS

    val stations: KStreamS[String, String] = builder.stream(config.kafka.station_topic)(Consumed.`with`(stringSerde, stringSerde))

    createStationStateSummary(stations)

    new KafkaStreams(builder.build(), streamingConfig)
  }

}