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

## Apache Zookeeper
[Apache ZooKeeper (aka. Zookeeper)](https://zookeeper.apache.org) is a distributed, open-source coordination service for distributed applications.
It exposes a simple set of primitives that distributed applications can build upon to implement higher level services for synchronization,
configuration maintenance, and groups and naming. It is designed to be easy to program to, and uses a data model
styled after the familiar directory tree structure of file systems.

ZooKeeper allows distributed processes to coordinate with each other through a shared hierarchal namespace which is organized similarly
to a standard file system. The name space consists of data registers - called znodes, in ZooKeeper parlance - and these are similar to
files and directories. Unlike a typical file system, which is designed for storage, ZooKeeper data is kept in-memory, which means
ZooKeeper can achieve high throughput and low latency numbers.

ZooKeeper is very fast and very simple. Since its goal, though, is to be a basis for the construction of more complicated services,
such as synchronization, it provides a set of guarantees. These are:

- __Sequential Consistency__: Updates from a client will be applied in the order that they were sent.
- __Atomicity__: Updates either succeed or fail. No partial results.
- __Single System Image__: A client will see the same view of the service regardless of the server that it connects to.
- __Reliability__: Once an update has been applied, it will persist from that time forward until a client overwrites the update.
- __Timeliness__: The clients view of the system is guaranteed to be up-to-date within a certain time bound.

ZooKeeper consists of multiple components:

- __Client__: Client is the Java client library, used by applications to connect to a ZooKeeper ensemble.
- __Server__: Server is the Java server that runs on the ZooKeeper ensemble nodes.

## Running Zookeeper
Zookeeper can run in [Clustered (Multi-Server) Setup](https://zookeeper.apache.org/doc/trunk/zookeeperAdmin.html#sc_zkMulitServerSetup) or
in [Single Server and Developer Setup](https://zookeeper.apache.org/doc/trunk/zookeeperAdmin.html#sc_singleAndDevSetup). In the [Lagom
developer environment (sbt project with lagom plugin)](http://www.lagomframework.com/documentation/1.3.x/scala/KafkaServer.html) a developer
zookeeper instance will be started when you type either `runAll` or `lagomKafkaStart`.

The [Confluent Platform](http://docs.confluent.io/3.1.2/) comes with a Zookeeper in the [distribution zip](https://www.confluent.io/download/)
and comes with a script to launch a local zookeeper instance. Setting up a ZooKeeper server in standalone mode is straightforward. The server
is contained in a single JAR file, so installation consists of creating a configuration. Once you've downloaded a stable ZooKeeper
release unpack it and cd to the root. To start ZooKeeper you need a configuration file for example the following comes from the confluent platform:

```
dataDir=/tmp/zookeeper
# the port at which the clients will connect
clientPort=2181
# disable the per-ip limit on the number of connections
# since this is a non-production config
maxClientCnxns=0
```

Both the vanilla zookeeper distribution or the confluent distribution has the same way to launch zookeeper in standalone mode,
there is a 'launch` script and a 'configuration script'. The vanilla zookeeper distribution uses the 'bin/zkServer.sh start' script
in where you put the config in the directory 'conf/zoo.cfg' and the confluent distribution lets you specify a config file to use:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/zookeeper-server-start ./etc/kafka/zookeeper.properties
```

For more information, [confluent.io has ideas on operating Zookeeper](http://kafka.apache.org/documentation/#zk) good
to know before installing a zookeeper on a platform.

## Apache Kafka
[Apache Kafka (aka. Kafka)](http://kafka.apache.org/) is an open-source stream processing platform developed by the Apache Software Foundation written in Scala and Java.
The project aims to provide a unified, high-throughput, low-latency platform for handling real-time data feeds. Its storage layer is
essentially a "massively scalable pub/sub message queue architected as a distributed transaction log making it highly valuable
for enterprise infrastructures to process streaming data. Additionally, Kafka connects to external systems (for data import/export)
via [Kafka Connect](http://docs.confluent.io/3.1.2/connect/index.html) and provides [Kafka Streams](http://docs.confluent.io/3.1.2/streams/index.html),
a Java stream processing library. The design of Kafka is heavily influenced by database transaction logs.

Kafka lets you:

- publish and subscribe to streams of records,
- store streams of records in a fault tolerant way,
- you process streams of records.

Kafka consists of two components:

- __Kafka Broker__: The Kafka broker that form the mesaging, data persistency and storage tier of Apache Kafka and usually run in a distributed cluster.
- __Kafka Java Client APIs__:
  - [Producer API](http://docs.confluent.io/3.1.2/clients/index.html#kafka-clients): A Java Client that allows an application to publish a stream records to one or more Kafka topics.
    For Scala a [reactive-streams compatible kafka connector 'reactive-kafka'](https://github.com/akka/reactive-kafka) is available that is maintained by the [Akka.io team](http://akka.io/)
    that makes publishing messages to Kafka a breeze.
  - [Consumer API](http://docs.confluent.io/3.1.2/clients/index.html#kafka-clients): A Java Client that allows an application to subscribe to one or more topics and process the stream of records produced to them.
    For Scala a [reactive-streams compatible kafka connector 'reactive-kafka'](https://github.com/akka/reactive-kafka) is available that is maintained by the [Akka.io team](http://akka.io/)
    that makes consuming messages from Kafka a breeze.
  - [Streams API](http://docs.confluent.io/3.1.2/streams/index.html#kafka-streams): Allows an application to act as a stream processor, consuming an input stream from one or more topics and producing an output stream to one or more output topics, effectively transforming the input streams to output streams. It has a very low barrier to entry, easy operationalization, and a high-level DSL for writing stream processing applications. As such it is the most convenient yet scalable option to process and analyze data that is backed by Kafka.
  - [Connect API](http://docs.confluent.io/3.1.2/connect/intro.html#connect-intro): A component to stream data between Kafka and other data systems in a scalable and reliable way. It makes it simple to configure connectors to move data into and out of Kafka. Kafka Connect can ingest entire databases or collect metrics from all your application servers into Kafka topics, making the data available for stream processing. Connectors can also deliver data from Kafka topics into secondary indexes like Elasticsearch or into batch systems such as Hadoop for offline analysis.

## Running Kafka
Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. Depending on your environment
for example, using Lagom you can just type `lagomKafkaStart` or `runAll` if you want to run everything of Lagom including Zookeeper,
Kafka and Cassandra. If you are using the confluent plaform distribution then you can just type:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/zookeeper-server-start ./etc/kafka/zookeeper.properties
```

To start Kafka you need a configuration. By default the following configuration is used when using the
confluent platform distribution:

```
# The id of the broker. This must be set to a unique integer for each broker.
broker.id=0

# The number of threads handling network requests
num.network.threads=3

# The number of threads doing disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600

# A comma seperated list of directories under which to store log files
log.dirs=/tmp/kafka-logs

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=1

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

# The minimum age of a log file to be eligible for deletion
log.retention.hours=1
#log.retention.hours=168

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
# 5 seconds
log.retention.check.interval.ms=5000
# 5 minutes
#log.retention.check.interval.ms=300000

zookeeper.connect=localhost:2181
zookeeper.connection.timeout.ms=6000
confluent.support.metrics.enable=true
confluent.support.customer.id=anonymous
```

You can launch kafka with the following command:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-server-start ./etc/kafka/server.properties
```

To launch a couple of

To create a topic called 'test':

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

We can query Kafka for topics with the following command:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-topics --list --zookeeper localhost:2181
```

We can consume from a topic using the following command:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
```

We can produce to a topic using the following command:

```bash
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-console-producer --broker-list localhost:9092 --topic test
```

## Kafka Messages
[Messages](http://kafka.apache.org/documentation/#messages) consist of a fixed-size header, a variable length opaque key byte array
and a variable length opaque value byte array. The header contains the following fields:

- A CRC32 checksum to detect corruption or truncation.
- A format version (magic byte)
- An attributes identifier
- A timestamp

Leaving the key and value opaque is the right decision: there is a great deal of progress being made on serialization libraries right now,
and any particular choice is unlikely to be right for all uses. Needless to say a particular application using Kafka would likely mandate
a particular serialization type as part of its usage.

```scala
/**
 * 1. 4 byte "CRC32" of the message
 * 2. 1 byte "magic" identifier to allow format changes, value is 0 or 1
 * 3. 1 byte "attributes" identifier to allow annotations on the message independent of the version
 *    bit 0 ~ 2 : Compression codec.
 *      0 : no compression
 *      1 : gzip
 *      2 : snappy
 *      3 : lz4
 *    bit 3 : Timestamp type
 *      0 : create time
 *      1 : log append time
 *    bit 4 ~ 7 : reserved
 * 4. (Optional) 8 byte timestamp only if "magic" identifier is greater than 0
 * 5. 4 byte key length, containing length K
 * 6. K byte key
 * 7. 4 byte payload length, containing length V
 * 8. V byte payload
 */
```

## Logs
A [log](http://kafka.apache.org/documentation/#log) for a topic named "my_topic" with two partitions consists of two directories
(namely my_topic_0 and my_topic_1) populated with data files containing the messages for that topic. The log (directories) will be
created in the directory that has been configured in Kafka and is the `log.dirs=/tmp/kafka-logs` key.

The format of the log files is a sequence of "log entries"; each log entry is a 4 byte integer N storing the message length
which is followed by the N message bytes. Each message is uniquely identified by a 64-bit integer offset giving the byte
position of the start of this message in the stream of all messages ever sent to that topic on that partition.

The on-disk format of each message is given below. Each log file is named with the offset of the first message it contains.
So the first file created will be 00000000000.kafka, and each additional file will have an integer name roughly S bytes
from the previous file where S is the max log file size given in the configuration.

The exact binary format for messages is versioned and maintained as a standard interface so message sets can be transferred
between producer, broker, and client without recopying or conversion when desirable. This format is as follows:

```
On-disk format of a message

offset         : 8 bytes
message length : 4 bytes (value: 4 + 1 + 1 + 8(if magic value > 0) + 4 + K + 4 + V)
crc            : 4 bytes
magic value    : 1 byte
attributes     : 1 byte
timestamp      : 8 bytes (Only exists when magic value is greater than zero)
key length     : 4 bytes
key            : K bytes
value length   : 4 bytes
value          : V bytes
```

To simplify the lookup structure we decided to use a simple per-partition atomic counter which could be coupled with
the partition id and node id to uniquely identify a message; this makes the lookup structure simpler, though multiple
seeks per consumer request are still likely. However once we settled on a counter, the jump to directly using the offset
seemed natural—both after all are monotonically increasing integers unique to a partition. Since the offset is hidden
from the consumer API this decision is ultimately an implementation detail and we went with the more efficient approach.

## Writes
The log allows [serial appends](http://kafka.apache.org/documentation/#impl_writes) which always go to the last file. This file is rolled over to a fresh file when it reaches a
configurable size (say 1GB). The log takes two configuration parameters: M, which gives the number of messages to write
before forcing the OS to flush the file to disk, and S, which gives a number of seconds after which a flush is forced.
This gives a durability guarantee of losing at most M messages or S seconds of data in the event of a system crash.

## Reads
Reads are done by giving the 64-bit logical offset of a message and an S-byte max chunk size. This will return an iterator
over the messages contained in the S-byte buffer. S is intended to be larger than any single message, but in the event of an
abnormally large message, the read can be retried multiple times, each time doubling the buffer size, until the message is read
successfully. A maximum message and buffer size can be specified to make the server reject messages larger than some size,
and to give a bound to the client on the maximum it needs to ever read to get a complete message. It is likely that the read buffer
ends with a partial message, this is easily detected by the size delimiting.

The actual process of reading from an offset requires first locating the log segment file in which the data is stored,
calculating the file-specific offset from the global offset value, and then reading from that file offset. The search is done
as a simple binary search variation against an in-memory range maintained for each file.

The log provides the capability of getting the most recently written message to allow clients to start subscribing as of
"right now". This is also useful in the case the consumer fails to consume its data within its SLA-specified number of
days. In this case when the client attempts to consume a non-existent offset it is given an OutOfRangeException and can either
reset itself or fail as appropriate to the use case.

## Deletes
Data is deleted one log segment at a time. The log manager allows pluggable delete policies to choose which files are eligible
for deletion. The current policy deletes any log with a modification time of more than N days ago, though a policy which retained
the last N GB could also be useful. To avoid locking reads while still allowing deletes that modify the segment list we use a
copy-on-write style segment list implementation that provides consistent views to allow a binary search to proceed on an
immutable static snapshot view of the log segments while deletes are progressing.

## Schema Registry
The [Schema Registry](https://github.com/confluentinc/schema-registry) is an open source product that provides a serving layer
for your metadata. It provides a RESTful interface for storing and retrieving Avro schemas. It stores a versioned history of all
schemas, provides multiple compatibility settings and allows evolution of schemas according to the configured compatibility setting.
It provides __serializers that plug into Kafka clients__ that handle schema storage and retrieval for Kafka messages
that are sent in the Avro format.

This way the Schema Registry enables safe, zero downtime evolution of schemas by centralizing the management of schemas
written for the Avro serialization system. It tracks all versions of schemas used for every topic in Kafka and only allows
evolution of schemas according to user-defined compatibility settings. This gives developers confidence that they can safely
modify schemas as necessary without worrying that doing so will break a different service they may not even be aware of.

The Schema Registry also includes plugins for Kafka clients that handle schema storage and retrieval for Kafka messages
that are sent in the Avro format. This integration is seamless, if you are already using Kafka with Avro data, using
the Schema Registry only requires including the serializers with your application and changing one setting.

## Running the Schema Registry
The Schema Registry is dependent on Zookeeper and Kafka and therefor both of them must be running. The schema registry is
part of the confluent platform and can be launched with the following command:

```
cd $CONFLUENT_PLATFORM_HOME
./bin/schema-registry-start ./etc/schema-registry/schema-registry.properties
```

The schema registry needs the following configuration:

```
listeners=http://0.0.0.0:8081
kafkastore.connection.url=localhost:2181
kafkastore.topic=_schemas
debug=false
```

## Schema Registry REST interface
The schema registry has a REST interface and can be accessed eg. by the [httpie](https://httpie.org/) CLI client
and is available at port 8081.

## Kafka REST Proxy
The Kafka REST Proxy provides a RESTful interface to a Kafka cluster. It makes it easy to produce and consume messages,
view the state of the cluster, and perform administrative actions without using the native Kafka protocol or clients.
Examples of use cases include reporting data to Kafka from any frontend app built in any language, ingesting messages into
a stream processing framework that doesn’t yet support Kafka, and scripting administrative actions.

The REST Proxy depends on: ZooKeeper, Kafka, and the Schema Registry so they all need to run.

The REST Proxy can be launched with the following command:

```
cd $CONFLUENT_PLATFORM_HOME
./bin/kafka-rest-start ./etc/kafka-rest/kafka-rest.properties
```

The REST Proxy needs the following configuration:

```
#id=kafka-rest-test-server
#schema.registry.url=http://localhost:8081
#zookeeper.connect=localhost:2181
```

The REST proxy is available at port `8082`.

### Listing topics
To list the available topics, execute the following command using [httpie]():

```
$ http :8082/topics
HTTP/1.1 200 OK
Content-Length: 153
Content-Type: application/vnd.kafka.v1+json
Date: Fri, 03 Feb 2017 19:49:26 GMT
Server: Jetty(9.2.12.v20150709)

[
    "test"
]
```

## List of brokers

```
$ http :8082/brokers
HTTP/1.1 200 OK
Content-Length: 15
Content-Type: application/vnd.kafka.v1+json
Date: Fri, 03 Feb 2017 19:57:15 GMT
Server: Jetty(9.2.12.v20150709)

{
    "brokers": [
        0
    ]
}
```

## Kafka Consumer
....

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

## Kafka Producer
....

## Kafka Security
[Kafka Security](http://docs.confluent.io/3.1.2/kafka/security.html#kafka-security) is supported from Kafka 0.9.0.0.
The Kafka community added a number of features that can be used, together or separately, to secure a Kafka cluster.

The following security measures are currently supported:

- Authentication of connections to brokers from clients (producers and consumers), other brokers and tools, using either
  SSL or SASL (Kerberos). SASL/PLAIN can also be used from release 0.10.0.0 onwards
- Authentication of connections from brokers to ZooKeeper
- Encryption of data transferred between brokers and clients, between brokers, or between brokers and tools using SSL
  (Note that there is a performance degradation when SSL is enabled, the magnitude of which depends on the CPU
  type and the JVM implementation)
- Authorization of read / write operations by clients
- Authorization is pluggable and integration with external authorization services is supported

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

## Kafka Streams
[Kafka Streams](http://docs.confluent.io/3.1.2/streams/architecture.html#streams-architecture) simplifies application development
by building on the Kafka producer and consumer libraries and leveraging the native capabilities of Kafka to offer data parallelism,
distributed coordination, fault tolerance, and operational simplicity.
    
## KStream
A [KStream](http://docs.confluent.io/3.1.2/streams/concepts.html#kstream-record-stream) is an abstraction of a record stream,
where each data record represents a self-contained datum in the unbounded data set.

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

## Kafka-Connect
Where Kafka-streams is focused on the Transformation part of eg. an ETL pipeline, Kafka connect takes the Extract and Load
part so the EL. Kafka Connect has been split into two parts, one part that does the 'extracting data from a system to Kafka' eg.
from a file, database, HDFS or even another Kafka, which is called the `source` and another part that takes the loading of data
from Kafka to a file, database, HDFS which is called the `sink`.

Kafka-connect comes bundled with the following connectors:

- [File Connector](http://docs.confluent.io/3.1.2/connect/connect-filestream/filestream_connector.html)
- [JDBC Connector](http://docs.confluent.io/3.1.2/connect/connect-jdbc/docs/index.html)
- [Elasticsearch Connector](http://docs.confluent.io/3.1.2/connect/connect-elasticsearch/docs/index.html)
- [HDFS Connector](http://docs.confluent.io/3.1.2/connect/connect-hdfs/docs/index.html)
- [Kafka Replicator Connector](http://docs.confluent.io/3.1.2/connect/connect-replicator/docs/index.html)

## Stream processing
Stream processing enables continuous, real-time processing and transformation of these streams and makes
the results available system-wide.

## Youtube
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