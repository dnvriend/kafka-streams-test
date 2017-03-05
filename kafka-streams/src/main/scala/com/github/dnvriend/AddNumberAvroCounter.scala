package com.github.dnvriend

import java.util.{ Properties, UUID }

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.state.{ KeyValueIterator, QueryableStoreTypes, ReadOnlyKeyValueStore }
import org.apache.kafka.streams.scaladsl.ScalaDsl._

object AddNumberAvroCounter extends App {
  def randomId(): String = UUID.randomUUID.toString
  final case class AddNumber(nr: Int)
  object AddNumber {
    implicit val recordFormat = RecordFormat[AddNumber]
  }

  final val StoreName = "addNumberCounterStore"

  implicit val builder: KStreamBuilder = new KStreamBuilder()
  implicit val config: Properties = KafkaConfig.config("AddNumberAvroCounter-" + randomId())

  builder.stream[String, GenericRecord]("AddNumberAvro")
    .parseFromAvro[AddNumber]
    .groupByKey()
    .count(StoreName)
  //    .toStream
  //    .foreach { (key, count) =>
  //      println(s"===> key='$key', count='$count'")
  //    }

  val streams = buildStreams(builder, config)
  streams.start()

  val keyValueStore: ReadOnlyKeyValueStore[String, Long] =
    streams.store(StoreName, QueryableStoreTypes.keyValueStore[String, Long]())
  //
  //
  //  val iter: KeyValueIterator[String, Long] = keyValueStore.all()
  //  while(iter.hasNext) {
  //    val next = iter.next()
  //    println("count for " + next.key + ": " + next.value)
  //  }

}
