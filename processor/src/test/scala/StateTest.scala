import java.util.Properties

import com.lightbend.kafka.scala.iq.serializers.Serializers
import com.lightbend.kafka.scala.streams.StreamsBuilderS
import io.circe.parser._
import io.circe.syntax._
import minitest.TestSuite
import models.{Serde, Station}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{LongSerializer, StringDeserializer, StringSerializer}
import org.apache.kafka.streams.kstream.{Materialized, Serialized}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.codehaus.jackson.map.deser.std.StdDeserializer.LongDeserializer
import server.{KafkaLocalServer, MessageListener, MessageSender, RecordProcessorTrait}
import state.StateAggregators
import state.StationStateProcessor.ACCESS_STATION_STATE
import utils.PathHelper
import versatile.utils.CirceHelper._

import scala.io.Source
import scala.util.Try

object StateTest extends TestSuite[KafkaLocalServer] with StateTestData {

  override def setup(): KafkaLocalServer = {
    val s = KafkaLocalServer(cleanOnStart = true, Some(localStateDir))
    println("starting local kafka")
    s.start()
    s
  }

  override def tearDown(server: KafkaLocalServer): Unit = {
    println("stopping local kafka")
    server.stop()
  }

  test("Test Logging DSL") { server =>
    Try(server.createTopic(inputTopic))
    Try(server.createTopic(outputTopic))

    //
    // Step 1: Configure and start the processor topology.
    //
    val streamsConfiguration = new Properties()
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, s"state-${scala.util.Random.nextInt(100)}")
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "wordcountgroup")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, localStateDir)

    val builder = new StreamsBuilderS()

    import com.lightbend.kafka.scala.streams.ImplicitConversions
    import com.lightbend.kafka.scala.streams.DefaultSerdes._

    val textLines = builder.stream[String, String](inputTopic)(ImplicitConversions.consumedFromSerde[String,String])

    val streamOutput = textLines.flatMapValues(parse(_).getRight.as[Station].right.toOption)
      .groupByKey(Serialized.`with`(stringSerde, Serde.STATION_SERDE))
      .count()
//      .aggregate(
//        () => Station.empty.asJson.noSpaces,
//        StateAggregators.foldStationState,
//        Materialized.`with`(stringSerde,stringSerde)
//      )
      .toStream
      .to(outputTopic)(ImplicitConversions.producedFromSerde[String, Long])

    val streams = new KafkaStreams(builder.build(), streamsConfiguration)
    streams.start()

    //
    // Step 2: Produce some input data to the input topic.
    //
    val sender = MessageSender[String, String](brokers, classOf[StringSerializer].getName, classOf[StringSerializer].getName)
    val mvals = input.foreach { case (key, value) =>
      sender.writeKeyValue(inputTopic, key, value.asJson.noSpaces)
    }

    //
    // Step 3: Verify the application's output data.
    //
    val listener = MessageListener(brokers,
      outputTopic,
      "wordcountgroup",
      classOf[StringDeserializer].getName,
      classOf[LongDeserializer].getName,
      new RecordProcessor)

    val l = listener.waitUntilMinKeyValueRecordsReceived(input.toMap.size, 10000)

    //    assertEquals(l.sortBy(_.key), input.sortBy(_.key))
    println("messages ========>")

    l.foreach(println)

    assert(l.nonEmpty)

    //    assertEquals(l.length, input.length)

    streams.close()
  }

  class RecordProcessor extends RecordProcessorTrait[String, Long] {
    override def processRecord(record: ConsumerRecord[String, Long]): Unit = {
      println(s"Get Message $record")
    }

  }

}

trait StateTestData {
  val inputTopic    = s"inputTopic"
  val outputTopic   = s"outputTopic"
  val brokers = "localhost:9092"
  val localStateDir = "local_state_data"

  val input: Seq[(String, Station)] = parse(Source.fromFile(s"${PathHelper.ProcessorReferenceTest}/consumer-output.json").getLines().mkString)
    .right.toSeq.flatMap(json => json.asArray.map(_.toSeq)).flatten
    .flatMap(jsonStation => jsonStation.as[Station].right.toSeq)
    .filter(_.contract_name == "Lyon")
    .groupBy(_.externalId)
    .take(3)
    .values
    .flatMap(stations => stations.map(station => station.externalId -> station)).toSeq
}
