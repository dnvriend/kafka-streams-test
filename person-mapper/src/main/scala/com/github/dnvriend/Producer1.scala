package com.github.dnvriend

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ ByteArraySerializer, StringSerializer }

import scala.concurrent.Future

object Producer1 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val done: Future[Done] = Source(1 to 10000)
    .map(i => s"""{"f1":"msg-$i"}""")
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("test", elem)
    }.runWith(Producer.plainSink(producerSettings))

  (for {
    _ <- done
    _ <- system.terminate()
  } yield ()).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}