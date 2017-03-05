name := "kafka-streams-test"

version in ThisBuild := "1.0.0"

scalaVersion in ThisBuild := "2.11.8"

organization in ThisBuild := "com.github.dnvriend"

val akkaVersion = "2.4.17"
val kafkaVersion = "0.10.2.0"
val confluentVersion = "3.2.0"

libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"

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
  (project in file("kafka-streams"))
   .enablePlugins(AutomateHeaderPlugin)

lazy val nativeClientTest = (project in file("native-client-test"))
  .settings(
    scalaVersion := "2.12.1",
    // https://github.com/sksamuel/avro4s
    libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4",
    libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.2.0",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.2.0",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
  ).enablePlugins(AutomateHeaderPlugin)

lazy val playProducer =
  (project in file("play-producer"))
  .enablePlugins(PlayScala)

lazy val googleClient =
  (project in file("google-client"))
  .enablePlugins(AutomateHeaderPlugin)