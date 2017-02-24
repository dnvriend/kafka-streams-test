package com.github.dnvriend

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.internals.ScalaKStreamBuilder
import play.api.libs.json.Json

object UpdatesTable extends App {
  final case class Update(name: String, count: Int)
  object Update {
    implicit val recordFormat = RecordFormat[Update]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)

  ScalaKStreamBuilder(KafkaConfig.config("updates-table"))
    .tableScalaDsl[String, Update]("Updatess", "UpdatesStore")
    .parseFromAvro[Update]
    .foreachScalaDsl { (key, value) =>
      println(s"==> [Update] ==> key='$key', value='$value'")
    }.start()
}
