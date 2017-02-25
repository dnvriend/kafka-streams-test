# Apache Zookeeper
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