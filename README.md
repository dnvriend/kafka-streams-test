# kafka-streams-test
A small study project on [Kafka-Streams](http://docs.confluent.io/3.1.2/streams) and the [Confluent Platform](http://docs.confluent.io/3.1.2/)
which is a stream data platform that leverages Apache Kafka to enable you to organize and manage data from many
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

- filter[K, V]((K, V) => Boolean): KStream[K, V]: Create a new instance of KStream that consists of all elements of this stream which satisfy a predicate.
- filterNot[K, V]((K, V) => Boolean): KStream[K, V]: Create a new instance of KStream that consists all elements of this stream which do not satisfy a predicate.
- foreach[K, V]((K, V) => Unit): Unit: Perform an action on each element of KStream. Note that this is a terminal operation that returns Unit
- map[K, V, R](K, V) => R): KStream[K, R]: Create a new instance of KStream by transforming each element in this stream into a different element in the new stream
- mapValues[K, V, R](V => R): KStream[K, R]: Create a new instance of KStream by transforming the value of each element in this stream into a new value in the new stream
- selectKey[K, V, R]((K, V) => R): KStream[K, R]: Create a new key from the current key and value.

Most of them can be applied to both [KStream](http://docs.confluent.io/3.1.2/streams/javadocs/index.html) and [KTable](http://docs.confluent.io/3.1.2/streams/javadocs/index.html),
where users usually pass a customized function to these functions as a parameter.

## Stateful transformations
Available [stateful transformations](http://docs.confluent.io/3.1.2/streams/developer-guide.html#stateful-transformations) include:

- joins (KStream/KTable): join, leftJoin, outerJoin
- aggregations (KStream/KTable): groupBy, groupByKey (KStream) plus count, reduce, aggregate (via KGroupedStream and KGroupedTable)
- general transformations (KStream): process, transform, transformValues

Stateful transformations are transformations where the processing logic requires accessing an associated state for processing
and producing outputs. For example, in join and aggregation operations, a windowing state is usually used to store all the
records received so far within the defined window boundary. The operators can then access accumulated records in the store and
compute based on them.
