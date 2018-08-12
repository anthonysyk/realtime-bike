name := "paris-velib"

version := "0.1"

scalaVersion := "2.11.8"

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
  .enablePlugins(PackPlugin)
  .settings(
    name := "collector",
    libraryDependencies ++= commonDependencies,
    scalaVersion := "2.11.8",
    packGenerateWindowsBatFile := false,
    packMain := Map("run-collector" -> "webservice.StationCollector"),
    packResourceDir := Map(
      baseDirectory.value / "Dockerfile" ->  "Dockerfile",
      baseDirectory.value / "src/main/bin" -> "bin"
    )
  )
  .dependsOn(
    common
  )

lazy val processor = project
  .enablePlugins(PackPlugin)
  .settings(
    name := "processor",
    libraryDependencies ++= commonDependencies,
    scalaVersion := "2.11.8",
    packGenerateWindowsBatFile := false,
    packMain := Map("run-processor" -> "state.StationStateProcessor"),
    packResourceDir := Map(
      baseDirectory.value / "Dockerfile" ->  "Dockerfile",
      baseDirectory.value / "src/main/bin" -> "bin"
    )
  )
  .dependsOn(
    common
  )

import Common.dependencies

lazy val commonDependencies = Seq(
  dependencies.versatile,
  dependencies.scala_test
) ++ dependencies.kafka_all ++ dependencies.akka_all ++ dependencies.circe

scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")