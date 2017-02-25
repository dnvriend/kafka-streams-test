name := "play-producer"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.4.17"
val kafkaVersion = "0.10.2.0"

// fp
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.9"
libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"

// kafka
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.1.2"
libraryDependencies += "org.apache.kafka" % "kafka-streams" % kafkaVersion
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4"

// ws
libraryDependencies += ws
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.16.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0-M1"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion

// test
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.11" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test

fork in Test := true

parallelExecution := false

enablePlugins(PlayScala)

// buildinfo
enablePlugins(BuildInfoPlugin)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoOptions += BuildInfoOption.ToMap

buildInfoOptions += BuildInfoOption.ToJson

buildInfoOptions += BuildInfoOption.BuildTime

buildInfoPackage := organization.value

// automate header
enablePlugins(AutomateHeaderPlugin)

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)


// scalariform
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
