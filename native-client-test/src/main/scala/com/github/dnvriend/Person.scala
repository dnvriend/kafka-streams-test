package com.github.dnvriend

final case class Person(id: String, name: String, age: Int, married: Option[Boolean] = None)