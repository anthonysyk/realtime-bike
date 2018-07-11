package state

import java.util.Properties

import com.lightbend.kafka.scala.streams.{KStreamS, StreamsBuilderS}
import config.AppConfig
import io.circe.parser._
import models.{Station, StationState}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.{Materialized, Serialized}
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import utils.SerdeHelper

object StationStateProcessor {

  final val HOST_INFO = new HostInfo("localhost", 9000)
  final val ACCESS_STATION_STATE = "access-station-state"

  import com.lightbend.kafka.scala.streams.DefaultSerdes._
  import com.lightbend.kafka.scala.streams.ImplicitConversions._

  val streamsConfiguration = new Properties()
  streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, s"state-${scala.util.Random.nextInt(100)}")
  streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "station-state")
  streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.brokers)
  streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "2000")
  // equivalent to from-beginning (reset the offset)
  streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val builder = new StreamsBuilderS

  val stations: KStreamS[String, String] = builder.stream[String, String](AppConfig.station_topic)

  import utils.CirceHelper._

  val stationSerde = SerdeHelper.createSerde[Station]

  def getAvailability(taken: Int, total: Int): Int = if(taken != 0) {100 - 100/(total/taken)} else 100

  stations.filter((k,v) => k == "217_Valence").foreach((k,v) => println(v))

  /* ACCESS_STATION_STATE */
  stations.flatMapValues(parse(_).getRight.as[Station].right.toOption)
    .groupByKey(Serialized.`with`(stringSerde, stationSerde))
    .aggregate(
      () => Station.empty,
      (_: String, current: Station, acc: Station) => {
        val deltaBikes = current.available_bikes - acc.available_bikes
        val bikesDropped = if(deltaBikes > 0) deltaBikes else 0
        val bikesTaken = if(deltaBikes < 0) Math.abs(deltaBikes) else 0
        val availability = getAvailability(current.available_bikes, current.bike_stands)
        current.copy(
          state = Some(StationState(
            bikes_taken = acc.state.map(_.bikes_taken + bikesTaken).getOrElse(0),
            bikes_droped = acc.state.map(_.bikes_droped + bikesDropped).getOrElse(0),
            availability = availability
          ))
        )
      },
      Materialized.as(ACCESS_STATION_STATE)
        .withKeySerde(stringSerde)
        .withValueSerde(stationSerde)
    ).toStream.foreach((x,y) => println(y))


  val streams = new KafkaStreams(builder.build, streamsConfiguration)

}