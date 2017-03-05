package com.github.dnvriend

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.scaladsl.{ Producer, Props }
import org.apache.kafka.streams.scaladsl.ScalaDsl._
import org.apache.kafka.streams.serde.{ ScalaLongSerde, ScalaLongSerializer }

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

final case class Number(nr: Long)
object Number {
  implicit val recordFormat = RecordFormat[Number]
}

object KafkaTableJoin2 extends App {
  implicit val props: java.util.Properties =
    Props("table-join-kafka-streams", "group1")
      .withKeySerde[StringSerde]()
      .withValueSerde[ScalaLongSerde]()
      .withRandomAppId()
      .props

  implicit val builder = new KStreamBuilder()

  final val TopicName = "longs"

  //
  val longs: KStream[String, Long] =
    builder
      .stream[String, Long](TopicName)

  // KTable: count by key on a ten second tumbling window
  val longCounts: KTable[Windowed[String], Long] =
    longs
      .groupByKey()
      .count(TimeWindows.of(10.seconds.toMillis), "longCounts")
      .mapValues[Long](_.toLong)

  // KTable: sum the values on a ten second tumbling window
  val longSums = {
    longs
      .groupByKey()
      .reduce((value1: Long, value2: Long) => value1 + value2, TimeWindows.of(10.seconds.toMillis), "longSums")
  }

  // KTable: join the two tables to get the averages
  val longAvgs = {
    longSums
      .join[Long, Double](longCounts, (value1: Long, value2: Long) => value1.toDouble / value2.toDouble)
      .foreach { (k, v) => println(s"====> $k, $v") }
  }

  val streams = buildStreams(builder, props)
  streams.start()

  implicit val producer: KafkaProducer[String, Long] =
    Producer.defaultProducer()
      .withValueSerializer[ScalaLongSerializer]()
      .producer[Long]

  import scala.concurrent.ExecutionContext.Implicits.global
  val f = Future.sequence(List("A", "B", "C").map { key =>
    produce(TopicName, key, 1L)
  }).map(println).recover { case t: Throwable => t.printStackTrace() }

  Await.ready(f, 10.seconds)
  Thread.sleep(5.seconds.toMillis)
}
