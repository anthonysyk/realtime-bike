name := "paris-velib"

version := "0.1"

scalaVersion := "2.11.8"

import Common.dependencies

lazy val commonDependencies = Seq(
  dependencies.versatile, dependencies.minitest, dependencies.minitestLaws
) ++ dependencies.kafka_all ++ dependencies.akka_all ++ dependencies.circe

lazy val kafkaStreamTestDependencies = Seq(
  dependencies.curator
)

lazy val sparkDependencies = Seq(
  dependencies.spark_all
).flatten

parallelExecution in Test := false
testFrameworks += new TestFramework("minitest.runner.Framework")


scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")



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
      baseDirectory.value / "Dockerfile" -> "Dockerfile",
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
    libraryDependencies ++= commonDependencies ++ kafkaStreamTestDependencies,
    scalaVersion := "2.11.8",
    testFrameworks += new TestFramework("minitest.runner.Framework"),
    packGenerateWindowsBatFile := false,
    packMain := Map("run-processor" -> "state.StationStateProcessor"),
    packResourceDir := Map(
      baseDirectory.value / "Dockerfile" -> "Dockerfile",
      baseDirectory.value / "src/main/bin" -> "bin"
    )
  )
  .dependsOn(
    common
  )

lazy val analyzer = project
  .enablePlugins(PackPlugin)
  .settings(
    name := "analyzer",
    libraryDependencies ++= commonDependencies ++ sparkDependencies,
    scalaVersion := "2.11.8",
    packGenerateWindowsBatFile := false,
    packMain := Map("run-analyzer" -> ""),
    packResourceDir := Map(
      baseDirectory.value / "Dockerfile" -> "Dockerfile",
      baseDirectory.value / "src/main/bin" -> "bin"
    )
  )
  .dependsOn(
    common
  )