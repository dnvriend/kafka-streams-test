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

import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{APPEND, CREATE, WRITE}
import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import scala.concurrent.{ExecutionContext, Future}

object GoogleRequestCreator extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()

  def writeFile(name: String, intersperseToken: String = "\n")(f: Long => String): Future[IOResult] =
    Source.repeat(1)
      .take(100000)
      .zipWithIndex.map { case (_, i) => f(i) }
      .map(str => ByteString(str))
      .intersperse(ByteString(intersperseToken))
      .runWith(FileIO.toPath(Paths.get(s"/tmp/connect.input/$name"), Set(WRITE, CREATE, APPEND)))

  val result: Future[_] = (for {
    _ <- writeFile("google.txt")(_ => UUID.randomUUID().toString)
    _ <- system.terminate()
  } yield println("done")).recoverWith {
    case t: Throwable =>
      t.printStackTrace()
      system.terminate()
  }
}
