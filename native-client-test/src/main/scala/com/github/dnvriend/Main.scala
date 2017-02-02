package com.github.dnvriend

import akka.actor.ActorSystem
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericData.Record
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }

import scala.concurrent.Future
import scala.concurrent._

object Main extends App {
  val system = ActorSystem()
  implicit val ec = system.dispatcher
  val producer = new KafkaProducer[String, Record](KafkaConfig.config)
  val key = "key1"
  val userSchema =
    """
      |{
      |  "type":"record",
      |  "name":"myrecord",
      |  "fields": [
      |    {"name": "f1", "type":"string"}
      |  ]
      |}
    """.stripMargin

  val parser = new Schema.Parser()
  val schema = parser.parse(userSchema)
  val avroRecord: Record = new GenericData.Record(schema)
  avroRecord.put("f1", "value1")
  val record = new ProducerRecord("topic1", key, avroRecord)
  (for {
    _ <- Future(blocking(producer.send(record).get))
    _ <- system.terminate()
  } yield ()).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}
