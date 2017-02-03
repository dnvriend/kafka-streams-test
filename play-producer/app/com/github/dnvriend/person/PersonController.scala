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
import play.api.mvc.{ Action, Controller }
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json.Json
import play.modules.kafka.KafkaProducer

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

final case class CreatePerson(name: String, age: Int)
object CreatePerson {
  implicit val format = Json.format[CreatePerson]
}

final case class CreatePersonCmd(id: String, name: String, age: Int)
case object CreatePersonCmd {
  implicit val format = Json.format[CreatePersonCmd]
}

class PersonController @Inject() (producer: KafkaProducer, cb: CircuitBreaker)(implicit ec: ExecutionContext) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  def randomId: String = UUID.randomUUID.toString

  def createPerson = Action.async { req =>
    val result = for {
      person <- Future.fromTry(Try(req.body.asJson.map(_.as[CreatePerson]).get))
      id = randomId
      cmd = CreatePersonCmd(id, person.name, person.age)
      _ <- producer.produce("test", id, cmd)
    } yield Ok(Json.toJson(cmd))

    cb.withCircuitBreaker(result)
  }
}
