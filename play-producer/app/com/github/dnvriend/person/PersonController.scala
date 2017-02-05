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

package com.github.dnvriend.person

import java.util.UUID
import javax.inject.Inject

import akka.pattern.CircuitBreaker
import com.sksamuel.avro4s.{ AvroSchema, RecordFormat }
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json.{ Format, Json }
import play.api.mvc.{ Action, AnyContent, Controller, Request }
import play.modules.kafka.KafkaProducer

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try
import scalaz.Scalaz._
import scalaz._

final case class CreatePerson(name: String, age: Int)
object CreatePerson {
  implicit val format = Json.format[CreatePerson]
}

final case class CreatePersonCmd(id: String, name: String, age: Int, married: Option[Boolean] = None, children: Int = 0)
case object CreatePersonCmd {
  implicit val format = Json.format[CreatePersonCmd]
  implicit val recordFormat = RecordFormat[CreatePersonCmd]
  implicit val schema = AvroSchema[CreatePersonCmd]
}

class PersonController @Inject() (producer: KafkaProducer, cb: CircuitBreaker)(implicit ec: ExecutionContext) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  def randomId: String = UUID.randomUUID.toString

  def entityAs[A: Format](implicit req: Request[AnyContent]): Try[A] =
    Try(req.body.asJson.map(_.as[A]).get)

  def createPerson = Action.async { implicit req =>
    val result = for {
      person <- Future.fromTry(entityAs[CreatePerson])
      id = randomId
      cmd = CreatePersonCmd(id, person.name, person.age)
      _ <- producer.produceJson("test", id, cmd) *> producer.produceAvro("test2", id, cmd)
    } yield Ok(Json.toJson(cmd))

    cb.withCircuitBreaker(result)
  }
}
