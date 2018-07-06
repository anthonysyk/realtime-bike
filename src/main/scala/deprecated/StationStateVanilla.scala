package deprecated

import java.util.Properties

import config.AppConfig
import io.circe.parser._
import models.Station
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.state.{KeyValueStore, QueryableStoreTypes, ReadOnlyKeyValueStore}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object StationStateVanilla {

  val streamingConfig: Properties = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stations")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "2000")
    props
  }

  def main(args: Array[String]): Unit = {

    val builder = new StreamsBuilder

    val kstream: KStream[Int, Array[Byte]] = builder.stream[Int, Array[Byte]](AppConfig.station_topic)
    //      .mapValues[String](value => new String(value))
    //      .filter((_, v) => v.isDefined)
    //      .mapValues[Station](_.get)

    //    table.groupBy((key: Int, value: Station) => new KeyValue[Int, Int](value.number, value.available_bikes))

    val stationStream: KStream[Int, Station] = kstream.mapValues[Option[Station]](value => parse(new String(value)).right.toOption.flatMap(_.as[Station].right.toOption))
      .filter((_, v) => v.isDefined)
      .mapValues[Station](_.get)

    stationStream.groupByKey().count(Materialized.as[Int, java.lang.Long, KeyValueStore[Bytes, Array[Byte]]]("CountsKeyValueStore"))

    val streams = new KafkaStreams(builder.build(), streamingConfig)
    streams.cleanUp()
    streams.start()
    println("Kafka Stream Application Started")

    Thread.sleep(10000)


    val keyValueStore: ReadOnlyKeyValueStore[Int, java.lang.Long] = streams.store("CountsKeyValueStore", QueryableStoreTypes.keyValueStore[Int, java.lang.Long]())

    println(keyValueStore.all().next())


//    while (true) {
//
//      Try(view) match {
//        case Success(v) => println(v.all().hasNext)
//        case Failure(e) => println(e)
//      }
//
//      Thread.sleep(5000)
//
//    }


  }


}
