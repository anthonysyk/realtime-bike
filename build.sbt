name := "paris-velib"

version := "0.1"

scalaVersion := "2.11.8"

organization := "org.versatile-flow"


import Common.dependencies

resolvers += "confluent" at "https://packages.confluent.io/maven/"

lazy val commonDependencies = Seq(
  dependencies.versatile
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

val commonSettings = Seq(
  scalaVersion := "2.11.8",
  packJarNameConvention := "full",
//  packExpandedClasspath := false,
  packGenerateWindowsBatFile := false
)


lazy val global = project
  .in(file("."))
  .aggregate(
    common,
    collector,
    processor
  )

lazy val common = project
  .settings(commonSettings)
  .settings(
    name := "common",
    libraryDependencies ++= commonDependencies
  )

lazy val collector = project
  .enablePlugins(PackPlugin)
  .settings(commonSettings)
  .settings(
    name := "collector",
    libraryDependencies ++= commonDependencies,
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
  .settings(commonSettings)
  .settings(
    name := "processor",
    libraryDependencies ++= commonDependencies,
    testFrameworks += new TestFramework("minitest.runner.Framework"),
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
  .settings(commonSettings)
  .settings(
    name := "analyzer",
    libraryDependencies ++= commonDependencies ++ sparkDependencies,
    packMain := Map("run-analyzer" -> ""),
    packResourceDir := Map(
      baseDirectory.value / "Dockerfile" -> "Dockerfile",
      baseDirectory.value / "src/main/bin" -> "bin"
    )
  )
  .dependsOn(
    common
  )
