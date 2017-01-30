package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{ ForeachAction, KStreamBuilder, ValueMapper }

import scala.language.implicitConversions

object Application2 extends App {

  implicit def functionToValueMapper[V1, V2](f: (V1) => V2): ValueMapper[V1, V2] =
    new ValueMapper[V1, V2] {
      override def apply(value: V1): V2 = f(value)
    }

  implicit def functionToForeachAction[K, V](f: (K, V) => Unit): ForeachAction[K, V] =
    new ForeachAction[K, V] {
      override def apply(key: K, value: V): Unit = f(key, value)
    }

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
    .foreach((key: Array[Byte], value: String) => println(s"==> MappedPersonCreated >><< key='$key', value='$value'"))
  new KafkaStreams(foreachBuilder, streamingConfig).start()
}
