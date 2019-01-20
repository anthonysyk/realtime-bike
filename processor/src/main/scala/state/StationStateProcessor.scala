package state

import java.time.Duration
import java.util.Properties
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import enums.WindowInterval
import http.{InteractiveQueryWorkflow, MyHTTPService}
import io.circe.syntax._
import models.{CustomSerde, Station, TopStation, WindowStation}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{Serde, Serdes, Serializer}
import org.apache.kafka.streams.kstream.{TimeWindows, Windowed}
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, ByteArrayWindowStore, StreamsBuilder}
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import utils.PathHelper
import utils.date.DateHelper
import versatile.json.CirceHelper._
import versatile.kafka.iq.http.{HttpRequester, KeyValueFetcher}
import versatile.kafka.iq.services.{LocalStateStoreQuery, MetadataService}

import scala.concurrent.ExecutionContext

object StationStateProcessor extends InteractiveQueryWorkflow {

  final val ACCESS_STATION_STATE = "access-station-state"

  final val TOP_STATION_STATE = "top-station-state"

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

    stationRecord.groupByKey.aggregate(Station.empty.asJson.noSpaces)({ (_, _, curr) => curr.asJson.noSpaces })(materialized)

  }

  def createTopStationsKTable(stationRecord: KStream[String, Station]): KTable[Windowed[String], String] = {

    implicit val serializer: Grouped[String, Station] = Grouped.`with`(_stringSerde, CustomSerde.STATION_SERDE)

    val materialized: Materialized[String, String, ByteArrayWindowStore] =
      Materialized.as(TOP_STATION_STATE)
        .withKeySerde(_stringSerde)
        .withValueSerde(_stringSerde)

    val window = Duration.ofHours(24)
    val grace = Duration.ofMinutes(10)

    type ExternalId = String
    type City = String

    import io.circe.jawn._
    import io.circe.syntax._


    def aggregation: (ExternalId, Station, String) => String = { (_, station, acc) =>
      val currentTopStation = parse(acc).getRight.as[TopStation].right.toOption.get

      val delta = currentTopStation.available_bikes - station.available_bikes
      val bikesDroped = if (delta < 0) currentTopStation.bikes_droped + Math.abs(delta) else currentTopStation.bikes_droped
      val bikesTaken = if (delta > 0) currentTopStation.bikes_taken + Math.abs(delta) else currentTopStation.bikes_taken

      TopStation(
        number = station.number,
        name = TopStation.cleanupName(station.contract_name, station.name),
        address = station.address,
        status = station.status,
        contract_name = station.contract_name,
        available_bike_stands = station.available_bike_stands,
        available_bikes = station.available_bikes,
        bikes_droped = if (currentTopStation.counter > 0) bikesDroped else 0,
        bikes_taken = if (currentTopStation.counter > 0) bikesTaken else 0,
        totalBikes = Nil, // currentTopStation.totalBikes :+ station.available_bikes,
        delta = Nil, //currentTopStation.delta :+ delta,
        counter = currentTopStation.counter + 1,
        start_date = if (currentTopStation.counter == 0) DateHelper.convertToReadable(station.last_update) else currentTopStation.start_date,
        last_update = DateHelper.convertToReadable(station.last_update),
        start_timestamp = if (currentTopStation.counter == 0) station.last_update else currentTopStation.start_timestamp
      ).asJson.noSpaces

    }

    stationRecord
      .groupByKey
      .windowedBy(TimeWindows.of(window))
      .aggregate(TopStation.empty.asJson.noSpaces)(aggregation)(materialized)


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

  def createStationStateWindow(stationRecord: KStream[String, Station], window: Duration, stateStoreName: String, topic: String): Unit = {

    implicit val serializer: Grouped[String, Station] = Grouped.`with`(_stringSerde, CustomSerde.STATION_SERDE)
    val materialized: Materialized[String, String, ByteArrayWindowStore] =
      Materialized.as(stateStoreName)(_stringSerde, _stringSerde)
        .withRetention(Duration.ofDays(3))

    val groupedStream = stationRecord.groupByKey

    /* WINDOW_STATION_STATE */
    groupedStream
      .windowedBy(TimeWindows.of(window))
      .aggregate(WindowStation.empty.asJson.noSpaces)(StateAggregators.foldWindowStation)(materialized)
    //      .toStream.map {
    //      case (k, v) => k.toString -> v
    //    }.to(topic)(Produced.`with`(_stringSerde, _stringSerde))

  }

  override def createStreams(): KafkaStreams = {
    // Kafka stream configuration
    val streamingConfig: Properties = {
      val streamsConfiguration = new Properties()
      streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, s"state-${
        scala.util.Random.nextInt(100)
      }")
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
      //      streamsConfiguration.put(StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, (86400000 * 7).toString)

      streamsConfiguration
    }


    val builder: StreamsBuilder = new org.apache.kafka.streams.scala.StreamsBuilder()

    implicit def stringSerde: Serde[String] = Serdes.String()

    val MAXIMUM_FROM = DateHelper.minusDaysTimestamp(3)
    val FLAG_STATION = "1020_Paris"

    val stations: KStream[String, Station] =
      builder.stream[String, GenericRecord](config.kafka.station_topic)(Consumed.`with`(stringSerde, CustomSerde.genericAvroSerde))
      .map {
        (_, v) =>
          val station = Station.avroFormat.from(v)
          if(station.externalId.contains(FLAG_STATION)) {
            val timestampDeltaUntilProcessing = station.last_update - MAXIMUM_FROM
            if(timestampDeltaUntilProcessing < 0) println(timestampDeltaUntilProcessing)
          }
          station.externalId -> station
      }.filter {
      (_, value) => Station.filteredContracts.contains(value.contract_name) && value.last_update > MAXIMUM_FROM
    }


    import WindowInterval._

    createStationStateSummary(stations)
    createStationStateWindow(stations, Duration.ofMinutes(15), WINDOW_STATION_STATE_15min, WINDOW_STATION_TOPIC_15min)
    createStationStateWindow(stations, Duration.ofMinutes(30), WINDOW_STATION_STATE_30min, WINDOW_STATION_TOPIC_30min)
    createStationStateWindow(stations, Duration.ofHours(1), WINDOW_STATION_STATE_1h, WINDOW_STATION_TOPIC_1h)
    createStationStateWindow(stations, Duration.ofHours(3), WINDOW_STATION_STATE_3h, WINDOW_STATION_TOPIC_3h)
    createStationStateWindow(stations, Duration.ofHours(6), WINDOW_STATION_STATE_6h, WINDOW_STATION_TOPIC_6h)

    createTopStationsKTable(stations)


    new KafkaStreams(builder.build(), streamingConfig)
  }

}
