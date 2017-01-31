package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder

import scala.language.implicitConversions

object Application2 extends App {
  var count = 0L

  val streamingConfig: Properties = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "application2")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
    // Specify default (de)serializers for record keys and for record values.
    settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings
  }

  // read the mappedPersonCreated
  val foreachBuilder: KStreamBuilder = new KStreamBuilder
  foreachBuilder.stream[Array[Byte], String]("MappedPersonCreated")
    .foreach { (key, value) =>
      count += 1
      println(s"==> [Application2 - $count] ==> key='$key', value='$value'")
    }
  new KafkaStreams(foreachBuilder, streamingConfig).start()
}
