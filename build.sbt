name := "kafka-streams-test"

version in ThisBuild := "1.0.0"

scalaVersion in ThisBuild := "2.11.8"

organization in ThisBuild := "com.github.dnvriend"

val akkaVersion = "2.4.17"

libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"

libraryDependencies += lagomScaladslApi
libraryDependencies += lagomScaladslPersistenceCassandra
libraryDependencies += lagomScaladslKafkaBroker

fork in Test in ThisBuild := true

parallelExecution in ThisBuild := false

resolvers in ThisBuild += "Confluent Maven Repo" at "http://packages.confluent.io/maven/"

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
    
licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2017", "Dennis Vriend"),
  "conf" -> Apache2_0("2017", "Dennis Vriend", "#")
)

enablePlugins(LagomScala)
enablePlugins(AutomateHeaderPlugin)

lagomKafkaCleanOnStart := true
lagomCassandraCleanOnStart := true

lazy val kafkaStreams =
  (project in file("kafka-streams")).settings(
    scalaVersion := "2.12.1",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.2.0",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13",
    libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.1.2",
    libraryDependencies += "io.confluent" % "monitoring-interceptors" % "3.1.2",
    libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4",
    libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M3",
//    libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.6.0-M1",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0-M1",
    libraryDependencies += "com.github.dnvriend" %% "kafka-streams-scala" % "1.0.0-SNAPSHOT"
  ).enablePlugins(AutomateHeaderPlugin)

lazy val nativeClientTest = (project in file("native-client-test"))
  .settings(
    scalaVersion := "2.12.1",
    // https://github.com/sksamuel/avro4s
    libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4",
    libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.1.2",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.1.1-cp1",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
  ).enablePlugins(AutomateHeaderPlugin)

lazy val playProducer =
  (project in file("play-producer"))
  .enablePlugins(PlayScala)

lazy val googleClient =
  (project in file("google-client"))
  .enablePlugins(AutomateHeaderPlugin)