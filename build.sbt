import sbtdocker.DockerPlugin.autoImport.dockerfile

name := "paris-velib"

version := "0.1"

scalaVersion := "2.11.8"

buildOptions in docker := BuildOptions(cache = false)

lazy val global = project
  .in(file("."))
  .aggregate(
    common,
    collector,
    processor
  )

lazy val common = project
  .settings(
    name := "common",
    libraryDependencies ++= commonDependencies,
    scalaVersion := "2.11.8"
  )

lazy val collector = project
  .enablePlugins(sbtdocker.DockerPlugin, JavaAppPackaging)
  .settings(
    name := "collector",
    libraryDependencies ++= commonDependencies,
    scalaVersion := "2.11.8",
    dockerfile in docker := {
      val appDir = "/collector/src/main"
      val targetDir = "/collector"
      new Dockerfile {
        from("hseeberger/scala-sbt")
        entryPoint(s"$targetDir/bin/${executableScriptName.value}")
        copyRaw(appDir, targetDir)
      }
    }
  )
  .dependsOn(
    common
  )

lazy val processor = project
  .enablePlugins(sbtdocker.DockerPlugin, JavaAppPackaging)
  .settings(
    name := "processor",
    libraryDependencies ++= commonDependencies,
    scalaVersion := "2.11.8",
    dockerfile in docker := {
      val appDir = "/processor/src/main"
      val targetDir = "/processor"
      new Dockerfile {
        from("hseeberger/scala-sbt")
        expose(9000)
        entryPoint(s"$targetDir/bin/${executableScriptName.value}")
        copyRaw(appDir, targetDir)
      }
    }
  )
  .dependsOn(
    common
  )

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

lazy val commonDependencies = Seq(
  dependencies.versatile,
  dependencies.scala_test
) ++ dependencies.kafka_all ++ dependencies.akka_all ++ dependencies.circe

scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")