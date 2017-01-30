# kafka-streams-test
A small study project on [Kafka-Streams](http://docs.confluent.io/3.1.2/streams)

## The project
To launch the project you need three terminal windows. The first terminal runs Lagom so you
should launch sbt and do a `runAll` to let the whole project launch. There is a PersonCreator
actor that sends a command to the PersonEntity to create one. The `PersonCreated` JSON serialized
event should be put on the `PersonCreated` topic.

Launch the lagom microservice:

```
sbt runAll
```

The project `person-mapper` contains two applications. `Application` does a very simple map on the
incoming PersonCreated events and saves the result to the `MappedPersonCreated` topic.

Launch the first application:

```
sbt "project personMapper" "runMain com.github.dnvriend.Application"
```

`Application2` reads these events and does a foreach, so it consumes the messages and logs them to the console.

Launch the second application:

```
sbt "project personMapper" "runMain com.github.dnvriend.Application2"
```