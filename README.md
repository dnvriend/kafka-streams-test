# kafka-streams-test
A small study project on [Kafka-Streams](http://docs.confluent.io/3.2.0/streams) and the [Confluent Platform](http://docs.confluent.io/3.2.0/)
which is a stream data platform that leverages [Apache Kafka](http://kafka.apache.org/) to enable you to organize and manage data from many
different sources with one reliable, high performance system. The Confluent Platform can be downloaded [here](https://www.confluent.io/download-center/).

## Requirements
Kafka running on `localhost:9092` and the ZooKeeper running on `localhost:2181`.

## The project
To launch the project you need three terminal windows. The first terminal runs Lagom so you
should launch sbt and do a `runAll` to let the whole project launch. There is a PersonCreator
actor that sends a command to the PersonEntity to create one. The `PersonCreated` JSON serialized
event should be put on the `PersonCreated` topic.

Launch the lagom microservice:

```
sbt clean runAll
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

## Kafka Ports
The following ports are in use by kafka:

Component  | Default Port
--- | ---
Zookeeper |	2181
Apache Kafka brokers (plain text) |	9092
Schema Registry REST API | 8081
REST Proxy | 8082
Kafka Connect REST API | 8083
Confluent Control Center | 9021

## Youtube
- [(0'59 hr) Building Streaming And Fast Data Applications With Spark, Mesos, Akka, Cassandra And Kafka - Sean Glover](https://www.youtube.com/watch?v=xRcD_ConPtg)
- [(0'04 hr) Kafka Streams Overview - Confluent.io](https://www.youtube.com/watch?v=V3m6ikfMxOQ)
- [(0'39 hr) ETL Is Dead, Long Live Streams: real-time streams w/ Apache Kafka - Neha Narkhede](https://www.youtube.com/watch?v=I32hmY4diFY)
- [(0'45 hr) Applications in the Emerging World of Stream Processing • Neha Narkhede](https://www.youtube.com/watch?v=WuBQBTET8Qg)
- [(0'29 hr) Apache Kafka and the Next 700 Stream Processing Systems - Jay Kreps](https://www.youtube.com/watch?v=9RMOc0SwRro)
- [(1'04 hr) I ♥ Logs: Apache Kafka and Real-Time Data Integration - Jay Kreps](https://www.youtube.com/watch?v=aJuo_bLSW6s)
- [(1'01 hr) Deep dive into Apache Kafka](https://www.youtube.com/watch?v=hplobfWsY2E)
- [(0'46 hr) Distributed stream processing with Apache Kafka - Jay Kreps](https://www.youtube.com/watch?v=rXzuZb3fHHM)
- [(1'30 hr) Developing Real-Time Data Pipelines with Apache Kafka - Joe Stein](https://www.youtube.com/watch?v=GRPLRONVDWY)
- [(0'55 hr) Microservices for a Streaming World | Kafka integration](https://www.youtube.com/watch?v=77huw-31oZg)
- [(0'58 hr) Streaming data integration with apache kafka](https://www.youtube.com/watch?v=t0yoKZizArA)
- [(0'37 hr) Introducing Kafka Streams, the new stream processing library - Michael Noll](https://www.youtube.com/watch?v=o7zSLNiTZbA)
- [(0'44 hr) Apache Kafka, Stream Processing, and Microservices - Jay Kreps](https://www.youtube.com/watch?v=4NLsncUPpDw)
- [(0'29 hr) Building Realtime Data Pipelines with Kafka Connect and Spark Streaming - Guozhang Wang](https://www.youtube.com/watch?v=pV4F-Fq5zdY)
- [(0'41 hr) Introducing Kafka Streams the New Stream Processing Library of Apache Kafka - Guozhang Wang](https://www.youtube.com/watch?v=QkDYkB6Q16Q)
- [(0'37 hr) Introducing Kafka Streams, the new stream processing library - Michael Noll](https://www.youtube.com/watch?v=o7zSLNiTZbA)
- [(0'35 hr) Building a Real-time Streaming Platform Using Kafka Streams - Jay Kreps](https://www.youtube.com/watch?v=zVK12q9PpQg)
- [(0'56 hr) Streaming operational data with Kafka – Couchbase](https://www.youtube.com/watch?v=L0SfRfKBRGA)

## Resources
- [Distributed, Real-time Joins and Aggregations on User Activity Events using Kafka Streams - Michael Noll](https://www.confluent.io/blog/distributed-real-time-joins-and-aggregations-on-user-activity-events-using-kafka-streams/)
- [Putting Apache Kafka to use: A practical guide to building a stream data platform - part 1/2 - Jay Kreps](https://www.confluent.io/blog/stream-data-platform-1/)
- [Putting Apache Kafka to use: A practical guide to building a stream data platform - part 2/2 - Jay Kreps](https://www.confluent.io/blog/stream-data-platform-2/)
- [Kafka 0.10.2.0 release notes](https://archive.apache.org/dist/kafka/0.10.2.0/RELEASE_NOTES.html)
- [Kafka Streams User Manual](http://docs.confluent.io/3.1.2/streams/index.html)