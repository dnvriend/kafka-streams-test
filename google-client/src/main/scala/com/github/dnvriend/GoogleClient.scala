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

package com.github.dnvriend

import java.util.UUID

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{ Consumer, Producer }
import akka.kafka.{ ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions }
import akka.stream.{ ActorMaterializer, Materializer }
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ Deserializer, Serializer }
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.{ AhcWSClient, AhcWSClientConfig }

import scala.concurrent.{ ExecutionContext, Future }

object GoogleClient extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  val ws = AhcWSClient(AhcWSClientConfig(maxRequestRetry = 0))(mat)

  def randomId: String = UUID.randomUUID.toString

  def producerSettings[K, V](system: ActorSystem, keySerializer: Option[Serializer[K]], valueSerializer: Option[Serializer[V]]): ProducerSettings[K, V] =
    ProducerSettings(system, keySerializer, valueSerializer)
      .withBootstrapServers("localhost:9092")

  def consumerSettings[K, V](system: ActorSystem, keySerializer: Option[Deserializer[K]], valueSerializer: Option[Deserializer[V]]) = {
    ConsumerSettings(system, keySerializer, valueSerializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId(randomId)
      .withClientId(randomId)
  }

  def callGoogle(ws: WSClient): Future[String] = {
    ws.url("https://www.google.nl").get().map(_.body)
  }

  def getGoogleData: Future[Done] = {
    Consumer.committableSource(consumerSettings[String, String](system, None, None), Subscriptions.topics("google-requests"))
      .mapAsync(1) { msg =>
        callGoogle(ws)
          .map(response => (msg, response))
      }.map {
        case (msg, response) => ProducerMessage.Message(new ProducerRecord[String, String]("google-responses", response), msg.committableOffset)
      }.runWith(Producer.commitableSink(producerSettings(system, None, None)))
  }

  (for {
    _ <- getGoogleData
    _ <- system.terminate()
  } yield println("done")).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}
