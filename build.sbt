name := "kafka-streams-test"

organization in ThisBuild := "com.github.dnvriend"

version := "1.0.0"

scalaVersion in ThisBuild := "2.11.8"

val akkaVersion = "2.4.16"

//libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"
//libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"
//libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"

libraryDependencies += lagomScaladslApi
libraryDependencies += lagomScaladslPersistenceCassandra
libraryDependencies += lagomScaladslKafkaBroker

fork in Test := true

parallelExecution := false

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
    
licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

enablePlugins(LagomScala)
enablePlugins(AutomateHeaderPlugin)

lagomKafkaCleanOnStart := true
lagomCassandraCleanOnStart := true

lazy val personMapper =
  (project in file("person-mapper")).settings(
    scalaVersion := "2.12.1",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.0.1" exclude("org.slf4j","slf4j-log4j12") exclude("javax.jms", "jms") exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri"),
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
  ).enablePlugins(AutomateHeaderPlugin)

lazy val nativeClientTest = (project in file("native-client-test"))
  .settings(
    scalaVersion := "2.12.1",
    resolvers += "Confluent Maven Repo" at "http://packages.confluent.io/maven/",
    // https://github.com/sksamuel/avro4s
    libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4",
    libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.1.2",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.1.1-cp1",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
  ).enablePlugins(AutomateHeaderPlugin)