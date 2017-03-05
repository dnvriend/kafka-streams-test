package com.github.dnvriend

import java.util.{ Properties, UUID }

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.{ KStreamBuilder, TimeWindows, Windows }
import org.apache.kafka.streams.kstream.internals.TimeWindow
import org.apache.kafka.streams.state.{ QueryableStoreTypes, ReadOnlyKeyValueStore }
import org.apache.kafka.streams.scaladsl.ScalaDsl._

import scala.concurrent.duration._

object AddNumberAvroReduce extends App {
  def randomId(): String = UUID.randomUUID.toString
  final case class AddNumber(nr: Int)
  object AddNumber {
    implicit val schema = RecordFormat[AddNumber]
    val append: (AddNumber, AddNumber) => AddNumber =
      (left: AddNumber, right: AddNumber) => AddNumber(left.nr + right.nr)
  }

  implicit val builder: KStreamBuilder = new KStreamBuilder()
  implicit val config: Properties = KafkaConfig.config("AddNumberAvroReduce-" + randomId())

  // at com.github.dnvriend.GenericAvroSerializer.serialize(GenericAvroSerializer.java:10)
  // typed serializer that knows how to deserialize the stuff from byte array and knows how to serialize the stuff
  // back again
  builder.stream[String, GenericRecord]("AddNumberAvro")
    .groupByKey()
    .reduce((x, y) => toAvro(AddNumber.append(fromAvro[AddNumber](x), fromAvro[AddNumber](y))), "reduce-counts")
    .toStream
    .parseFromAvro[AddNumber]
    .runForeach { (key, count) =>
      println(s"===> key='$key', count='$count'")
    }
}
