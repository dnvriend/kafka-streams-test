name := "kafka-streams-test"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.16"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"
libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"
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
    scalaVersion := "2.11.8",
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.12",
    libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.0.1" exclude("org.slf4j","slf4j-log4j12") exclude("javax.jms", "jms") exclude("com.sun.jdmk", "jmxtools") exclude("com.sun.jmx", "jmxri")
  ).enablePlugins(AutomateHeaderPlugin)
