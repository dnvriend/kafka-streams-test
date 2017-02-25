package com.github.dnvriend

import java.util.{ Properties, UUID }

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.internals.ScalaDsl._

object AddNumberAvroForeach extends App {
  def randomId(): String = UUID.randomUUID.toString
  final case class AddNumber(nr: Int)
  object AddNumber {
    implicit val recordFormat = RecordFormat[AddNumber]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  var count = 0L

  implicit val builder: KStreamBuilder = new KStreamBuilder()
  implicit val config: Properties = KafkaConfig.config("AddNumberAvroForeach-" + randomId())

  builder.stream[String, GenericRecord]("AddNumberAvro")
    .parseFromAvro[AddNumber]
    .runForeach { (key, value) =>
      count += 1
      println(s"==> [AddNumberAvroForeach - $count] ==> key='$key', value='$value'")
    }
}
