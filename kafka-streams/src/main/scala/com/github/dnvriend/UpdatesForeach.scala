package com.github.dnvriend

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.internals.ScalaDsl._

object UpdatesForeach extends App {

  final case class Update(name: String, count: Int)

  object Update {
    implicit val recordFormat = RecordFormat[Update]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  var count = 0L

  implicit val builder = new KStreamBuilder()
  implicit val config = KafkaConfig.config("updates-foreach")

  builder.stream[String, GenericRecord]("Updatess")
    .parseFromAvro[Update]
    .runForeach { (key, value) =>
      count += 1
      println(s"==> [PersonForeachMapper - $count] ==> key='$key', value='$value'")
    }
}
