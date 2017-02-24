package com.github.dnvriend

import java.util.UUID

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.Json

import scala.compat.Platform
import scala.concurrent.Future
import scala.util.Random

object UpdateProducer extends App {
  final case class Update(name: String, count: Int)
  object Update {
    implicit val recordFormat = RecordFormat[Update]
  }

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val producerSettings: ProducerSettings[String, GenericRecord] = ProducerSettings[String, GenericRecord](system, None, None)
    .withBootstrapServers("localhost:9092")

  def randomId: String = UUID.randomUUID.toString

  def genericRecord[A](value: A)(implicit recordFormat: RecordFormat[A]): GenericRecord = recordFormat.to(value)

  def record[A: RecordFormat](topic: String, key: String, value: A): ProducerRecord[String, GenericRecord] =
    new ProducerRecord(topic, key, genericRecord(value))

  val sink: Sink[ProducerRecord[String, GenericRecord], Future[Done]] =
    Producer.plainSink(producerSettings)

  val done =
    Source.repeat(1)
      .take(10)
      .map(value => record("Updatess", "foo", Update("foo", 1)))
      .runWith(sink)

  val start = Platform.currentTime
  (for {
    _ <- done
    _ <- system.terminate()
  } yield println("took: " + (Platform.currentTime - start) + " millis")).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}
