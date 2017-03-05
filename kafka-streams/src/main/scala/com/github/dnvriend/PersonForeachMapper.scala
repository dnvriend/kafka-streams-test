package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.scaladsl.ScalaDsl._

import scala.language.implicitConversions

object PersonForeachMapper extends App {

  final case class PersonCreated(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)

  object PersonCreated {
    implicit val recordFormat = RecordFormat[PersonCreated]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  var count = 0L

  implicit val builder = new KStreamBuilder()
  implicit val config = KafkaConfig.config("person-foreach-mapper")

  builder.stream[String, GenericRecord]("MappedPersonCreatedAvro")
    .parseFromAvro[PersonCreated]
    .runForeach { (key, value) =>
      count += 1
      println(s"==> [PersonForeachMapper - $count] ==> key='$key', value='$value'")
    }
}
