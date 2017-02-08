package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder

import scala.language.implicitConversions

object PersonForeachMapper extends App {
  final case class PersonCreated(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)
  object PersonCreated {
    implicit val recordFormat = RecordFormat[PersonCreated]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  var count = 0L

  // read the mappedPersonCreated
  val foreachBuilder: KStreamBuilder = new KStreamBuilder
  foreachBuilder.stream[String, GenericRecord]("MappedPersonCreatedAvro")
    .mapValues[PersonCreated](record => recordAs[PersonCreated](record))
    .foreach { (key, value) =>
      count += 1
      println(s"==> [PersonForeachMapper - $count] ==> key='$key', value='$value'")
    }
  new KafkaStreams(foreachBuilder, KafkaConfig.config("personcreated-logger")).start()
}
