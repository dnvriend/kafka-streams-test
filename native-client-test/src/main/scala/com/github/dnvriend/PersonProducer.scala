package com.github.dnvriend

import java.util.UUID

import akka.actor.ActorSystem
import com.sksamuel.avro4s.{ AvroSchema, RecordFormat }
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }

import scala.concurrent.{ Future, _ }
import scala.util.Random

object PersonProducer extends App {
  def uuid = UUID.randomUUID.toString
  val names = List("John Doe", "Jane Doe")
  def name(xs: List[String]): String = xs.drop(Random.nextInt(xs.length)).headOption.getOrElse("John Unknown")
  val system = ActorSystem()
  implicit val ec = system.dispatcher
  val producer = new KafkaProducer[String, GenericRecord](KafkaConfig.config)
  val key = "key1"
  val schema: Schema = AvroSchema[Person]
  val format: RecordFormat[Person] = RecordFormat[Person]

  def sendRecord(xs: List[String]): Future[_] = Future {
    blocking {
      val avroRecord: GenericRecord = format.to(Person(uuid, name(names), Random.nextInt(100), Option(false)))
      val record = new ProducerRecord(Topic.PersonCreated, key, avroRecord)
      producer.send(record).get
    }
  }

  (for {
    _ <- Future.sequence(Range.inclusive(1, 10).map(_ => sendRecord(names)))
    _ <- system.terminate()
  } yield ()).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}
