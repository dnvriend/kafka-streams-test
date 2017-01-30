package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import java.util.{ Properties, UUID }

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{ ForeachAction, KStreamBuilder, ValueMapper }
import play.api.libs.json._

import scala.language.implicitConversions

object PersonCreated {
  implicit val format = Json.format[PersonCreated]
}

final case class PersonCreated(id: UUID, name: String, age: Int, time: Long)

object Application extends App {
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
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "application1")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
    // Specify default (de)serializers for record keys and for record values.
    settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings
  }
  val builder: KStreamBuilder = new KStreamBuilder
  // read the input to a KStream instance
  builder
    .stream[Array[Byte], String]("PersonCreated")
    .mapValues { (x: String) =>
      Json.parse(x).as[PersonCreated]
    }.mapValues { (e: PersonCreated) =>
      e.copy(age = 100)
    }.mapValues { (e: PersonCreated) =>
      val json = Json.toJson(e).toString
      println(s"Putting on topic: '$json'")
      json
    }.to("MappedPersonCreated")

  new KafkaStreams(builder, streamingConfig).start()
}