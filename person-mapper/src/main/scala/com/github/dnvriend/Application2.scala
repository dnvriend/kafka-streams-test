package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder

import scala.language.implicitConversions

object Application2 extends App {
  var count = 0L

  // read the mappedPersonCreated
  val foreachBuilder: KStreamBuilder = new KStreamBuilder
  foreachBuilder.stream[String, String]("MappedPersonCreated")
    .foreach { (key, value) =>
      count += 1
      println(s"==> [Application2 - $count] ==> key='$key', value='$value'")
    }
  new KafkaStreams(foreachBuilder, KafkaConfig.config("personcreated-logger")).start()
}
