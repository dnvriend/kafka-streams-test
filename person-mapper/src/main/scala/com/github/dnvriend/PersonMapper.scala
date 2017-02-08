package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder

import scala.io.Source
import scala.util.Random

object PersonMapper extends App {
  final case class PersonCreated(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)
  object PersonCreated {
    implicit val recordFormat = RecordFormat[PersonCreated]
  }

  def recordAs[A](record: GenericRecord)(implicit format: RecordFormat[A]): A = format.from(record)
  def valueToRecord[A](value: A)(implicit format: RecordFormat[A]): GenericRecord = format.to(value)

  def getData(resource: String): List[String] =
    Source.fromInputStream(this.getClass.getResourceAsStream(resource))
      .getLines()
      .map(_.split(","))
      .flatMap(_.headOption)
      .toList

  val lastNames: List[String] = getData("/census-dist-2500-last.csv")

  val names: List[String] = getData("/census-dist-female-first.csv") ++ getData("/census-dist-male-first.csv")

  def random(xs: List[String]): String = xs.drop(Random.nextInt(xs.length)).headOption.getOrElse("No entry")

  var count = 0L

  val builder: KStreamBuilder = new KStreamBuilder

  builder
    .stream[String, GenericRecord]("PersonCreatedAvro")
    .mapValues[PersonCreated](record => recordAs[PersonCreated](record))
    .mapValues(event => event.copy(name = s"${random(names)} ${random(lastNames)}"))
    .mapValues[GenericRecord](value => valueToRecord(value))
    .to("MappedPersonCreatedAvro")

  new KafkaStreams(builder, KafkaConfig.config("person-mapper")).start()
}