package com.github.dnvriend

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.{ LongSerializer, StringSerializer }
import org.apache.kafka.common.serialization.Serdes.{ LongSerde, StringSerde }
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.scaladsl.ScalaDsl._
import org.apache.kafka.streams.scaladsl.{ Producer, Props }
import org.apache.kafka.streams.serde.ScalaLongSerializer

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

object KafkaTableJoin3 extends App {
  type Long = java.lang.Long

  //  implicit val props: java.util.Properties =
  //    Props("table-join-kafka-streams", "group1")
  //      .withKeySerde[StringSerde]()
  //      .withValueSerde[LongSerde]()
  //      .withRandomAppId()
  //      .props
  //
  //  implicit val builder = new KStreamBuilder()
  //
  final val TopicName = "longs"
  //
  //  val longs: KStream[String, Long] =
  //    builder
  //      .stream[String, Long](TopicName)
  //
  //  longs.foreach((k, v) => s"==> longs key=$k, value=$v`")
  //
  //  // KTable: count by key on a ten second tumbling window
  //  val longCounts: KTable[Windowed[String], Long] =
  //    longs
  //      .groupByKey()
  //      .count(TimeWindows.of(1.seconds.toMillis), "longCounts")
  //
  //  longCounts.foreach((k, v) => s"==> LongCounts key=$k, value=$v")
  //
  //  // KTable: sum the values on a ten second tumbling window
  //  val longSums = {
  //    longs
  //      .groupByKey()
  //      .reduce(((value1, value2) => value1 + value2): Reducer[Long], TimeWindows.of(1.seconds.toMillis), "longSums")
  //  }
  //
  //  longSums.foreach((k, v) => s"==> LongSums key=$k, value=$v")
  //
  //  // KTable: join the two tables to get the averages
  //  val longAvgs =
  //    longSums
  //      .join[Long, Double](longCounts, (value1: Long, value2: Long) => value1.toDouble / value2.toDouble)
  //
  //  longAvgs.foreach { (k, v) => println(s"====> LongAvgs key=$k, value=$v")}
  //
  //  longAvgs.to("longavgs")
  //
  //  val streams = buildStreams(builder, props)
  //  streams.start()

  implicit val producer =
    Producer.defaultProducer()
      .withValueSerializer[LongSerializer]()
      .producer[Long]

  import scala.concurrent.ExecutionContext.Implicits.global

  val sequenceFuture = Future.sequence(List("A", "B", "C").map { key =>
    produce[Long](TopicName, key + 1, 12345L)
  })

  val f = (for {
    result <- sequenceFuture
    //    _ <- Future(streams.close())
    _ <- Future(producer.close())
  } yield {
    println("ready: " + result)
  }).recover { case t: Throwable => t.printStackTrace() }

  Await.ready(f, 10.seconds)
  //  println("Sleeping")
  //  Thread.sleep(10.seconds.toMillis)
  //  println("Closing app")
}
