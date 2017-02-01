# kafka-streams-test
A small study project on [Kafka-Streams](http://docs.confluent.io/3.1.2/streams) and the [Confluent Platform](http://docs.confluent.io/3.1.2/)
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

## Kafka Streams
[Kafka Streams](http://docs.confluent.io/3.1.2/streams/architecture.html#streams-architecture) simplifies application development
by building on the Kafka producer and consumer libraries and leveraging the native capabilities of Kafka to offer data parallelism,
distributed coordination, fault tolerance, and operational simplicity.

## KStream
A [KStream](http://docs.confluent.io/3.1.2/streams/concepts.html#kstream-record-stream) is an abstraction of a record stream, where each data record represents a self-contained datum in the unbounded data set.

## KTable
A [KTable](http://docs.confluent.io/3.1.2/streams/concepts.html#ktable-changelog-stream) is an abstraction of a changelog stream,
where each data record represents an update. More precisely, the value in a data record is considered to be an update of the last
value for the same record key, if any (if a corresponding key doesn’t exist yet, the update will be considered a create).

Using the table analogy, a data record in a changelog stream is interpreted as an update because any existing row with the same key is overwritten.

KTable also provides an ability to look up current values of data records by keys. This table-lookup functionality is available through
[join operations](http://docs.confluent.io/3.1.2/streams/concepts.html#streams-concepts-joins) and [Joining Streams](http://docs.confluent.io/3.1.2/streams/developer-guide.html#streams-developer-guide-dsl-joins).

## Stream Joins on Key
A [join operation](http://docs.confluent.io/3.1.2/streams/concepts.html#joins) merges two streams based on __the keys__ of their data records,
and yields a new stream. A join over record streams usually needs to be performed on a windowing basis because otherwise the number of records
that must be maintained for performing the join may grow indefinitely.

Depending on the operands the following join operations are [supported](http://docs.confluent.io/3.1.2/streams/developer-guide.html#joining-streams):

| Join operands   	 | (INNER) JOIN	| OUTER JOIN  | LEFT JOIN |
| ------------------ | ------------ | ----------- | --------- |
| KStream-to-KStream | Supported    | Supported   |	Supported |
| KTable-to-KTable	 | Supported	| Supported	  | Supported |
| KStream-to-KTable	 | N/A	        | N/A	      | Supported |

Since stream joins are performed over __the keys of records__, it is required that joining streams are co-partitioned by key,
i.e., their corresponding Kafka topics must have the same number of partitions and partitioned on the same key so that records
with the same keys are delivered to the same processing thread. This is validated by Kafka Streams library at runtime.

## Aggregations
An [aggregation operation](http://docs.confluent.io/3.1.2/streams/concepts.html#aggregations) takes one input stream,
and yields a new stream by combining multiple input records into a single output record. Examples of aggregations are
computing counts or sum. An aggregation over record streams usually needs to be performed on a windowing basis because
otherwise the number of records that must be maintained for performing the aggregation may grow indefinitely.

## Stateless transformations
Stateless transformations include:

```
- filter[K, V]((K, V) => Boolean): KStream[K, V]: Create a new instance of KStream that consists of all elements of this stream which satisfy a predicate.
- filterNot[K, V]((K, V) => Boolean): KStream[K, V]: Create a new instance of KStream that consists all elements of this stream which do not satisfy a predicate.
- foreach[K, V]((K, V) => Unit): Unit: Perform an action on each element of KStream. Note that this is a terminal operation that returns Unit
- map[K, V, R](K, V) => R): KStream[K, R]: Create a new instance of KStream by transforming each element in this stream into a different element in the new stream
- mapValues[K, V, R](V => R): KStream[K, R]: Create a new instance of KStream by transforming the value of each element in this stream into a new value in the new stream
- selectKey[K, V, R]((K, V) => R): KStream[K, R]: Create a new key from the current key and value.
```

Most of them can be applied to both [KStream](http://docs.confluent.io/3.1.2/streams/javadocs/index.html) and [KTable](http://docs.confluent.io/3.1.2/streams/javadocs/index.html),
where users usually pass a customized function to these functions as a parameter.

## Stateful transformations
Available [stateful transformations](http://docs.confluent.io/3.1.2/streams/developer-guide.html#stateful-transformations) include:

```
- joins (KStream/KTable): join, leftJoin, outerJoin
- aggregations (KStream/KTable): groupBy, groupByKey (KStream) plus count, reduce, aggregate (via KGroupedStream and KGroupedTable)
- general transformations (KStream): process, transform, transformValues
```

Stateful transformations are transformations where the processing logic requires accessing an associated state for processing
and producing outputs. For example, in join and aggregation operations, a windowing state is usually used to store all the
records received so far within the defined window boundary. The operators can then access accumulated records in the store and
compute based on them.

## ConsumerRecord
A ConsumerRecord represents a key/value pair that has been received from Kafka by a Consumer. The ConsumerRecord consists
of a topic name, a partition number from which the record has been received and an offset that points to the record in
a Kafka partition. Of course the record also contains the key and the value:

```scala
case class ConsumerRecord[K, V](topic: String, partition: Int, offset: Long, key: K, value: V)
```

Each ConsumerRecord has a `key: K` and a `value: V`. Kafka needs to know how to deserialize the
Key and the Value and this is done by configuring a key.deserializer/value.deserializer for the consumer.
Both the key and the value could be a simple String type, in that case you could just configure a StringDeserializer.

## Timestamp Extractor
A [timestamp extractor](http://docs.confluent.io/3.1.2/streams/developer-guide.html#streams-developer-guide-timestamp-extractor) extracts a
timestamp from an instance of ConsumerRecord. Timestamps are used to control the progress of streams.

The default extractor is [ConsumerRecordTimestampExtractor](https://github.com/apache/kafka/blob/0.10.1/streams/src/main/java/org/apache/kafka/streams/processor/ConsumerRecordTimestampExtractor.java).
This extractor retrieves built-in timestamps that are automatically embedded into Kafka messages by the Kafka producer client (v0.10.0.0).
Depending on the setting of Kafka’s `log.message.timestamp.type` parameter, this extractor will provide you with:

- event-time processing semantics if log.message.timestamp.type is set to CreateTime aka “producer time” (which is the default). This represents the time when the Kafka producer sent the original message.
- ingestion-time processing semantics if log.message.timestamp.type is set to LogAppendTime aka “broker time”. This represents the time when the Kafka broker received the original message.

Another built-in extractor is [WallclockTimestampExtractor](https://github.com/apache/kafka/blob/0.10.1/streams/src/main/java/org/apache/kafka/streams/processor/WallclockTimestampExtractor.java).
This extractor does not actually “extract” a timestamp from the consumed record but rather returns the current time in milliseconds
from the system clock, which effectively means Streams will operate on the basis of the so-called processing-time of events.

You can also provide your own timestamp extractors, for instance to retrieve timestamps embedded in the payload of messages by
extending the `TimestampExtractor`class and then configuring the `StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG`.

## Youtube
- [(0'29 hr) Apache Kafka and the Next 700 Stream Processing Systems - Jay Kreps](https://www.youtube.com/watch?v=9RMOc0SwRro)
- [(1'04 hr) I ♥ Logs: Apache Kafka and Real-Time Data Integration - Jay Kreps](https://www.youtube.com/watch?v=aJuo_bLSW6s)
- [(0'46 hr) Distributed stream processing with Apache Kafka - Jay Kreps](https://www.youtube.com/watch?v=rXzuZb3fHHM)
- [(1'30 hr) Developing Real-Time Data Pipelines with Apache Kafka - Joe Stein](https://www.youtube.com/watch?v=GRPLRONVDWY)
- [(0'44 hr) Apache Kafka, Stream Processing, and Microservices - Jay Kreps](https://www.youtube.com/watch?v=4NLsncUPpDw)
- [(0'29 hr) Building Realtime Data Pipelines with Kafka Connect and Spark Streaming - Guozhang Wang](https://www.youtube.com/watch?v=pV4F-Fq5zdY)
- [(0'41 hr) Introducing Kafka Streams the New Stream Processing Library of Apache Kafka - Guozhang Wang](https://www.youtube.com/watch?v=QkDYkB6Q16Q)
- [(0'37 hr) Introducing Kafka Streams, the new stream processing library - Michael Noll](https://www.youtube.com/watch?v=o7zSLNiTZbA)
- [(0'35 hr) Building a Real-time Streaming Platform Using Kafka Streams - Jay Kreps](https://www.youtube.com/watch?v=zVK12q9PpQg)
