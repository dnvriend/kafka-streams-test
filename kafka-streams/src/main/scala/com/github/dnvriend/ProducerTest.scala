package com.github.dnvriend

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.scaladsl.Producer
import org.apache.kafka.streams.scaladsl.ScalaDsl._

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

object ProducerTest extends App {
  implicit val producer =
    Producer.avroProducer()
      .producer[GenericRecord]

  import scala.concurrent.ExecutionContext.Implicits.global
  val f = Future.sequence((1 to 100).map { key =>
    produceAvro("longsAvro3", key.toString, Number(1))
  }).recover { case t: Throwable => t.printStackTrace() }

  val g = for {
    xs <- f
    _ <- Future(producer.close())
  } yield ()

  Await.ready(g, 10.seconds)
}
