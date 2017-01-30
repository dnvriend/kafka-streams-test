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

package impl

import java.util.UUID
import javax.inject.Inject

import akka.actor.{ Actor, ActorSystem, Props }
import api.{ CreatePersonRequestMessage, PersonService, TopicMessagePersonCreated }
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence._
import play.api.libs.json.Json

import scala.compat.Platform
import scala.concurrent.ExecutionContext
import scala.util.Random

class LagomPersonService @Inject() (persistentEntityRegistry: PersistentEntityRegistry, system: ActorSystem)(implicit ec: ExecutionContext) extends PersonService {
  val creator = system.actorOf(Props(new PersonCreator(persistentEntityRegistry)))

  override def createPerson: ServiceCall[CreatePersonRequestMessage, UUID] = ServiceCall { (person: CreatePersonRequestMessage) =>
    val id: UUID = UUID.randomUUID()
    val idAsString: String = id.toString
    val ref = persistentEntityRegistry.refFor[PersonEntity](idAsString)
    ref.ask(CreatePerson(id, person.name, person.age, Platform.currentTime))
  }

  override def personCreatedTopic: Topic[TopicMessagePersonCreated] = TopicProducer.singleStreamWithOffset { offset =>
    persistentEntityRegistry.eventStream(PersonCreated.Tag, offset)
      .map {
        case EventStreamElement(entityId, PersonCreated(id, name, age, time), offset) =>
          val event = TopicMessagePersonCreated(id, name, age, time)
          println(s"Publishing event: '$event' with offset: '$offset' for entityId: '$entityId'")
          (event, offset)
      }
  }
}

class PersonCreator(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends Actor {
  import scala.concurrent.duration._
  override def preStart(): Unit = {
    context.system.scheduler.schedule(5.seconds, 2.second, self, "create")
  }
  override def receive: Receive = {
    case _ =>
      val id: UUID = UUID.randomUUID()
      val idAsString: String = id.toString
      val ref = persistentEntityRegistry.refFor[PersonEntity](idAsString)
      val cmd = CreatePerson(id, s"foo$id", Random.nextInt(100), Platform.currentTime)
      println(s"===> PersonCreator creating person: $cmd")
      ref.ask(cmd)
  }
}

class PersonEntity extends PersistentEntity {
  override type Command = PersonCommand[_]
  override type Event = PersonEvent
  override type State = Option[Person]
  override def initialState: Option[Person] = Option.empty[Person]

  override def behavior: Behavior = {
    // a persistent entity can define different behaviors for different states

    // state = empty person
    case None => Actions().onCommand[CreatePerson, UUID] {
      // command handler
      case (cmd: CreatePerson, ctx, state) =>
        //        println(s"===> Handling create person for: '$entityId', entityName: '$entityTypeName', state: $state")
        val (newState, event) = Person.handleCommand(state, cmd)
        ctx.thenPersist(event) { _ =>
          ctx.reply(cmd.id)
        }
    }.onEvent {
      case (event: PersonEvent, state) =>
        //        println(s"===> Rebuilding state for entityId: '$entityId', entityName: '$entityTypeName', state: $state")
        Person.handleEvent(state, event)
    }
  }
}

// commands
sealed trait PersonCommand[R] extends ReplyType[R]
object CreatePerson {
  implicit val format = Json.format[CreatePerson]
}
final case class CreatePerson(id: UUID, name: String, age: Int, time: Long) extends PersonCommand[UUID]

// events
sealed trait PersonEvent

object PersonCreated {
  implicit val format = Json.format[PersonCreated]
  val Tag: AggregateEventTag[PersonCreated] = AggregateEventTag[PersonCreated]("person-created")
}

final case class PersonCreated(id: UUID, name: String, age: Int, time: Long) extends PersonEvent with AggregateEvent[PersonCreated] {
  override def aggregateTag = PersonCreated.Tag
}

// state
object Person {
  implicit val format = Json.format[Person]
  def time: Long = Platform.currentTime
  def handleCommand(state: Option[Person], cmd: PersonCommand[_]): (Option[Person], PersonEvent) = cmd match {
    case CreatePerson(id, name, age, _) => (Option(Person(id, name, age)), PersonCreated(id, name, age, time))
  }

  def handleEvent(state: Option[Person], event: PersonEvent): Option[Person] = event match {
    case PersonCreated(id, name, age, _) => Option(Person(id, name, age))
  }
}

final case class Person(id: UUID, name: String, age: Int)
