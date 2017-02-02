package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder

// see: https://github.com/confluentinc/examples/tree/3.1.x/kafka-streams/src/main/java/io/confluent/examples/streams/utils
object PersonConsumer extends App {
  var count = 0L
  val foreachBuilder: KStreamBuilder = new KStreamBuilder
  val format: RecordFormat[Person] = RecordFormat[Person]
  foreachBuilder.stream[Array[Byte], GenericRecord](Topic.PersonCreated)
    .foreach { (key, record) =>
      count += 1
      val person: Person = format.from(record)
      println(s"==> [PersonConsumer - $count] ==> key='$key', value='$person'")
    }
  new KafkaStreams(foreachBuilder, KafkaStreamingConfig.config("person-logger")).start()
}
