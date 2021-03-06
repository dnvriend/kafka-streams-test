/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.topics

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.pattern.CircuitBreaker
import akka.stream.{ ActorMaterializer, IOResult, Materializer }
import akka.stream.scaladsl.{ FileIO, Source }
import akka.util.ByteString
import com.sksamuel.avro4s.{ AvroSchema, RecordFormat }
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json.{ Format, Json }
import play.api.mvc._
import play.modules.kafka.KafkaProducer

import scala.compat.Platform
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try
import scalaz.syntax.applicative._
import scalaz.std.scalaFuture._
import TopicsUtil._

final case class CreatePerson(name: String, age: Long)
object CreatePerson {
  implicit val format = Json.format[CreatePerson]
}

final case class CreatePersonCmd(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)
case object CreatePersonCmd {
  implicit val format = Json.format[CreatePersonCmd]
  implicit val recordFormat = RecordFormat[CreatePersonCmd]
  implicit val schema = AvroSchema[CreatePersonCmd]
}

final case class AddNumber(nr: Int)

object AddNumber {
  implicit val format = Json.format[AddNumber]
  implicit val schema = AvroSchema[AddNumber]
}

object TopicsUtil {
  def randomId(): String = UUID.randomUUID.toString
  def now(): String = {
    val date = new java.util.Date()
    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date)
  }
}

class TopicsController @Inject() (producer: KafkaProducer, cb: CircuitBreaker, components: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(components) {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  def createPerson = Action(parse.json[CreatePerson]).async { (req: Request[CreatePerson]) =>
    val result = for {
      person <- Future(req.body)
      id <- Future(randomId())
      cmd = CreatePersonCmd(id, person.name, person.age.toInt)
      _ <- producer.produceJson("PersonCreatedJson", id, cmd) *> producer.produceAvro("PersonCreated", id, cmd)
    } yield Ok(Json.toJson(cmd))

    cb.withCircuitBreaker(result)
  }

  def addNumber = Action(parse.json[AddNumber]).async { (req: Request[AddNumber]) =>
    val result = for {
      _ <- producer.produceJson("AddNumber", "number", req.body) *> producer.produceAvro("AddNumberAvro", "number", req.body)
    } yield Ok(Json.toJson(req.body))

    cb.withCircuitBreaker(result)
  }
}

object PersonCreator extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  def writeFile(name: String, intersperseToken: String = "\n")(f: Long => String): Future[IOResult] = Source.repeat(1).zipWithIndex.map {
    case (_, i) => f(i)
  }.map(str => ByteString(str))
    .intersperse(ByteString(intersperseToken))
    .take(100000)
    .runWith(FileIO.toPath(Paths.get(s"/tmp/connect.input/$name"), Set(WRITE, CREATE, APPEND)))

  (for {
    _ <- writeFile("test.json")(_ => Json.toJson(CreatePerson(UUID.randomUUID().toString, Platform.currentTime)).toString)
    _ <- writeFile("test.sql", ",")(_ => s"('${UUID.randomUUID()}', ${Platform.currentTime})")
    _ <- system.terminate()
  } yield println("done")).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}