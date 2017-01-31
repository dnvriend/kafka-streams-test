package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder
import spray.json.{ DefaultJsonProtocol, _ }

import scala.io.Source
import scala.language.implicitConversions
import scala.util.Random

final case class PersonCreated(id: String, name: String, age: Int, time: Long)

// see: http://deron.meranda.us/data/
object Application extends App with DefaultJsonProtocol {
  implicit val format = jsonFormat4(PersonCreated)

  def getData(resource: String): List[String] =
    Source.fromInputStream(this.getClass.getResourceAsStream(resource))
      .getLines()
      .map(_.split(","))
      .flatMap(_.headOption)
      .toList

  val streamingConfig: Properties = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "application1")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
    // Specify default (de)serializers for record keys and for record values.
    settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings
  }
  val lastNames: List[String] = getData("/census-dist-2500-last.csv")

  val names: List[String] = getData("/census-dist-female-first.csv") ++ getData("/census-dist-male-first.csv")

  def random(xs: List[String]): String = xs.drop(Random.nextInt(xs.length)).headOption.getOrElse("No entry")

  val builder: KStreamBuilder = new KStreamBuilder
  // read the input to a KStream instance
  builder
    .stream[Array[Byte], String]("PersonCreated")
    .mapValues[PersonCreated](_.parseJson.convertTo[PersonCreated])
    .mapValues(event => (event, event.copy(name = s"${random(names)} ${random(lastNames)}")))
    .mapValues[String] {
      case (old, event) =>
        val json = event.toJson.prettyPrint
        println(
          s"""==> [Application]:
          |oldEvent: $old
          |mapped: $event
          |Publishing to 'MappedPersonCreated':
          |asJson: $json
        """.stripMargin
        )
        json
    }.to("MappedPersonCreated")

  new KafkaStreams(builder, streamingConfig).start()
}