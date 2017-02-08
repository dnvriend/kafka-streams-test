package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.KStreamBuilder
import spray.json.{ DefaultJsonProtocol, _ }

import scala.io.Source
import scala.language.implicitConversions
import scala.util.Random

final case class PersonCreated(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)

// see: http://deron.meranda.us/data/
// see: http://docs.confluent.io/3.1.2/streams/developer-guide.html#streams-developer-guide-dsl
object Application extends App with DefaultJsonProtocol {
  implicit val format = jsonFormat5(PersonCreated)

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
  // read the input to a KStream instance
  builder
    .stream[String, String]("PersonCreatedJson")
    .mapValues[String] { e => println(e); e }
    .mapValues[PersonCreated](_.parseJson.convertTo[PersonCreated])
    .mapValues(event => (event, event.copy(name = s"${random(names)} ${random(lastNames)}")))
    .mapValues[String] {
      case (old, event) =>
        count += 1
        val json = event.toJson.prettyPrint
        println(
          s"""==> [Application]:
          |count: $count
          |oldEvent: $old
          |mapped: $event
          |Publishing to 'MappedPersonCreated':
          |asJson: $json
        """.stripMargin
        )
        json
    }.to("MappedPersonCreated")

  new KafkaStreams(builder, KafkaConfig.config("person-mapper")).start()
}