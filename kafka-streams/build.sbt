scalaVersion := "2.12.1"

val kafkaVersion = "0.10.2.0"
val confluentVersion = "3.2.0"

libraryDependencies += "org.apache.kafka" % "kafka-streams" % kafkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.13"
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % confluentVersion
libraryDependencies += "io.confluent" % "monitoring-interceptors" % confluentVersion
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4"
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M3"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0-M1"
libraryDependencies += "com.github.dnvriend" %% "kafka-streams-scala" % "1.0.0-SNAPSHOT"