package com.github.dnvriend

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.internals.ScalaKStreamBuilder

object UpdatesForeach extends App {
  final case class Update(name: String, count: Int)
  object Update {
    implicit val recordFormat = RecordFormat[Update]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  var count = 0L

  ScalaKStreamBuilder(KafkaConfig.config("updates-foreach"))
    .streamScalaDsl[String, GenericRecord]("Updatess")
    .parseFromAvro[Update]
    .foreach { (key, value) =>
      count += 1
      println(s"==> [PersonForeachMapper - $count] ==> key='$key', value='$value'")
    }.start()
}
