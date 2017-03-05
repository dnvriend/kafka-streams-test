package com.github.dnvriend

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}

abstract class TestSpec extends FlatSpec with Matchers {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()


}
