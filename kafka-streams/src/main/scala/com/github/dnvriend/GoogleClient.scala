package com.github.dnvriend

// see: https://github.com/confluentinc/examples
// see: https://github.com/confluentinc/kafka/tree/trunk/streams/src
// see: http://docs.confluent.io/3.1.2/streams/index.html

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.scaladsl.ScalaDsl._
import play.api.libs.ws.ahc._

import scala.concurrent.{ ExecutionContext, Future }

object GoogleClient extends App {

  final case class PersonCreated(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)

  object PersonCreated {
    implicit val format = RecordFormat[PersonCreated]
  }

  final case class GoogleResults(str: String)

  object GoogleResults {
    implicit val format = RecordFormat[GoogleResults]
  }

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  val ws = StandaloneAhcWSClient(AhcWSClientConfig(maxRequestRetry = 0))(mat)

  def callGoogle(ws: StandaloneAhcWSClient): Future[GoogleResults] = {
    ws.url("https://www.google.nl").get().map(_.body)
  }.map(GoogleResults.apply)

  def randomId: String = UUID.randomUUID().toString

  implicit val builder = new KStreamBuilder()
  implicit val config = KafkaConfig.config("google-results-" + randomId)

  builder.stream[String, GenericRecord]("PersonCreatedAvro")
    .parseFromAvro[PersonCreated]
    .mapAsync(_ => callGoogle(ws))
    .mapToAvro
    .runTopic("GoogleResults")
}