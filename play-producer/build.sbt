name := "play-producer"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.16"

// fp
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"
libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"

// kafka
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "3.1.2"
libraryDependencies += "org.apache.kafka" % "kafka-streams" % "0.10.1.1-cp1"
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4"

// ws
libraryDependencies += ws
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.14.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.10"
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.5.3"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion

// database support
libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "com.zaxxer" % "HikariCP" % "2.5.1"
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.2"
// database driver
libraryDependencies += "com.h2database" % "h2" % "1.4.193"


// test
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.6.8" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test

fork in Test := true

parallelExecution := false

enablePlugins(PlayScala)
