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

package play.modules.kafka

import javax.inject.{ Inject, Singleton }

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.pattern.CircuitBreaker
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import play.api.libs.json.{ Format, Json }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class KafkaProducer @Inject() (cb: CircuitBreaker)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {
  val producerSettings: ProducerSettings[String, String] =
    ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")

  def produce(producerRecord: ProducerRecord[String, String]): Future[Done] =
    cb.withCircuitBreaker(Source.single(producerRecord).runWith(Producer.plainSink(producerSettings)))

  def produce[A: Format](topic: String, key: String, value: A): Future[Done] = {
    produce(new ProducerRecord[String, String](topic, key, Json.toJson(value).toString))
  }
}
