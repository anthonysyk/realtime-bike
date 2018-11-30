import Versions._
import sbt._

object Common {
  lazy val dependencies = {
    new {

      val curator      = "org.apache.curator"         % "curator-test"    % CuratorVersion
      val minitest     = "io.monix"                   %% "minitest"       % MinitestVersion % "test"
      val minitestLaws = "io.monix"                   %% "minitest-laws"  % MinitestVersion % "test"

      val kafka_all = Seq(
        "org.apache.kafka" % "kafka_2.11" % kafkaV excludeAll (ExclusionRule("org.slf4j", "slf4j-log4j12"), ExclusionRule("org.apache.zookeeper",
          "zookeeper")),
        "org.apache.kafka" % "kafka-streams" % kafkaV excludeAll (ExclusionRule("org.slf4j", "slf4j-log4j12"), ExclusionRule("org.apache.zookeeper",
          "zookeeper")),
        "com.lightbend" %% "kafka-streams-scala" % kafka_streams_scala_version,
        "com.lightbend" %% "kafka-streams-query" % kafka_streams_query_version,
        "net.manub" %% "scalatest-embedded-kafka" % "1.0.0"
      )

      val akka_all = Seq(
        "com.typesafe.akka" %% "akka-http" % "10.1.0",
        "com.typesafe.akka" %% "akka-stream" % "2.5.11"
      )

      val spark_all = Seq(
        "org.apache.spark" %% "spark-core" % "2.2.0",
        "org.apache.spark" %% "spark-sql" % "2.2.0",
        "org.apache.spark" %% "spark-mllib" % "2.2.0" % "runtime"
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
