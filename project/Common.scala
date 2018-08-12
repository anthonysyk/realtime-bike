import sbt._
import Keys._

object Common {
  lazy val dependencies = {
    new {
      val kafkaV = "1.0.1"
      val kafka_streams_scala_version = "0.2.1"
      val kafka_streams_query_version = "0.1.1"
      val circeVersion = "0.9.1"

      val scala_test = "org.scalatest" %% "scalatest" % "3.0.5" % "test"


      val kafka_all = Seq(
        "org.apache.kafka" % "kafka_2.11" % kafkaV,
        "org.apache.kafka" % "kafka-streams" % kafkaV,
        "com.lightbend" %% "kafka-streams-scala" % kafka_streams_scala_version,
        "com.lightbend" %% "kafka-streams-query" % kafka_streams_query_version,
        "net.manub" %% "scalatest-embedded-kafka" % "1.0.0"
      )

      val akka_all = Seq(
        "com.typesafe.akka" %% "akka-http" % "10.1.0",
        "com.typesafe.akka" %% "akka-stream" % "2.5.11"
      )

      val circe = Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser"
      ).map(_ % circeVersion)

      val versatile = "org.versatile-flow" %% "versatile-library" % "0.1"
    }
  }
}
