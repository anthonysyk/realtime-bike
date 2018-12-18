package state

import java.time.Duration
import java.util.Properties
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import enums.WindowInterval
import http.{InteractiveQueryWorkflow, MyHTTPService}
import io.circe.syntax._
import models.{CustomSerde, Station}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{Serde, Serdes, Serializer}
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, ByteArrayWindowStore, StreamsBuilder}
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import utils.PathHelper
import versatile.kafka.iq.http.{HttpRequester, KeyValueFetcher}
import versatile.kafka.iq.services.{LocalStateStoreQuery, MetadataService}

import scala.concurrent.ExecutionContext

object StationStateProcessor extends InteractiveQueryWorkflow {

  final val ACCESS_STATION_STATE = "access-station-state"

  implicit val _stringSerde: Serde[String] = Serdes.String()
  implicit val _stringSerializer: Serializer[String] = Serdes.String().serializer()

  def main(args: Array[String]): Unit = workflow()

  override def startRestProxy(streams: KafkaStreams, hostInfo: HostInfo,
                              actorSystem: ActorSystem, materializer: ActorMaterializer): MyHTTPService = {

    implicit val system: ActorSystem = actorSystem

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

    val fetcher = new StateFetcher(
      new KeyValueFetcher[String, String](metadataService, localStateStoreQuery, httpRequester, streams, executionContext, hostInfo)
    )

    val restService = new MyHTTPService(
      hostInfo,
      system, materializer, executionContext,
      fetcher
    )
    restService.start()
    restService
  }

  def createLastStateKTable(stationRecord: KStream[String, Station]): KTable[String, String] = {

    implicit val serializer: Grouped[String, Station] = Grouped.`with`(_stringSerde, CustomSerde.STATION_SERDE)

    val materialized: Materialized[String, String, ByteArrayKeyValueStore] =
      Materialized.as(ACCESS_STATION_STATE)
        .withKeySerde(_stringSerde)
        .withValueSerde(_stringSerde)

    stationRecord.groupByKey.aggregate(Station.empty.asJson.noSpaces)({ (_,_, curr) => curr.asJson.noSpaces})(materialized)

  }

  def createStationStateSummary(stationRecord: KStream[String, Station]) = {

    implicit val serializer: Grouped[String, Station] = Grouped.`with`(_stringSerde, CustomSerde.STATION_SERDE)

    val materialized: Materialized[String, String, ByteArrayKeyValueStore] =
      Materialized.as(ACCESS_STATION_STATE)
        .withKeySerde(_stringSerde)
        .withValueSerde(_stringSerde)

    val groupedStream = stationRecord.groupByKey

    /* ACCESS_STATION_STATE */
    groupedStream
      .aggregate(Station.empty.asJson.noSpaces)(StateAggregators.foldStationState)(materialized)
      .toStream.to(config.kafka.station_state_topic)(Produced.`with`(_stringSerde, _stringSerde))

  }

  def createStationStateWindow(stationRecord: KStream[String, Station], window: Duration, stateStoreName: String, topic: String) = {

    implicit val serializer: Grouped[String, Station] = Grouped.`with`(_stringSerde, CustomSerde.STATION_SERDE)
    val materialized: Materialized[String, String, ByteArrayWindowStore] =
      Materialized.as(stateStoreName)
        .withKeySerde(_stringSerde)
        .withValueSerde(_stringSerde)

    val groupedStream = stationRecord.groupByKey

    /* WINDOW_STATION_STATE */
    groupedStream
      .windowedBy(TimeWindows.of(window))
      .aggregate(Station.empty.asJson.noSpaces)(StateAggregators.foldStationState)(materialized)
      .toStream.map{case (k,v) => k.toString -> v}.to(topic)(Produced.`with`(_stringSerde, _stringSerde))

  }

  override def createStreams(): KafkaStreams = {
    // Kafka stream configuration
    val streamingConfig: Properties = {
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

      streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, config.webservice.hostport)

      // default is /tmp/kafka-streams
      streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, PathHelper.StateStoreDirectory)

      // Set the commit interval to 500ms so that any changes are flushed frequently and the summary
      // data are updated with low latency.
      streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500")

      streamsConfiguration
    }


    val builder: StreamsBuilder = new org.apache.kafka.streams.scala.StreamsBuilder()

    implicit def stringSerde: Serde[String] = Serdes.String()

    val stations = builder.stream[String, GenericRecord](config.kafka.station_topic)(Consumed.`with`(stringSerde, CustomSerde.genericAvroSerde))
      .map((k, v) => k -> Station.avroFormat.from(v))

    import WindowInterval._
    createStationStateSummary(stations)
    createStationStateWindow(stations, Duration.ofMinutes(5), WINDOW_STATION_STATE_5min, WINDOW_STATION_TOPIC_5min)
    createStationStateWindow(stations, Duration.ofMinutes(15), WINDOW_STATION_STATE_15min, WINDOW_STATION_TOPIC_15min)
    createStationStateWindow(stations, Duration.ofMinutes(30), WINDOW_STATION_STATE_30min, WINDOW_STATION_TOPIC_30min)
    createStationStateWindow(stations, Duration.ofHours(1), WINDOW_STATION_STATE_1h, WINDOW_STATION_TOPIC_1h)
    createStationStateWindow(stations, Duration.ofHours(3), WINDOW_STATION_STATE_3h, WINDOW_STATION_TOPIC_3h)
    createStationStateWindow(stations, Duration.ofHours(12), WINDOW_STATION_STATE_12h, WINDOW_STATION_TOPIC_12h)
    createStationStateWindow(stations, Duration.ofDays(1), WINDOW_STATION_STATE_1j, WINDOW_STATION_TOPIC_1j)


    new KafkaStreams(builder.build(), streamingConfig)
  }

}
