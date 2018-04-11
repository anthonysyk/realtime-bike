name := "paris-velib"

version := "0.1"

scalaVersion := "2.11.8"

val kafkaV = "1.0.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % kafkaV,
  "org.apache.kafka" % "kafka-streams" % kafkaV,
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")