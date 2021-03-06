package com.github.dnvriend

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.serde.avro.GenericAvroSerde

object KafkaConfig {
  def configAsMap(applicationId: String): Map[String, String] = Map(
    // An identifier for the stream processing application. Must be unique within the Kafka cluster.
    // Each stream processing application must have a unique id. The same id must be given to all instances of the application.
    // It is recommended to use only alphanumeric characters, . (dot), - (hyphen), and _ (underscore). Examples: "hello_world", "hello_world-v1.0.0"
    //
    // This id is used in the following places to isolate resources used by the application from others:
    //
    // - As the default Kafka consumer and producer client.id prefix
    // - As the Kafka consumer group.id for coordination
    // - As the name of the sub-directory in the state directory (cf. state.dir)
    // - As the prefix of internal Kafka topic names
    // see: http://docs.confluent.io/3.1.2/streams/developer-guide.html#id24
    StreamsConfig.APPLICATION_ID_CONFIG -> applicationId,

    // A list of host/port pairs to use for establishing the initial connection to the Kafka cluster
    //
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    // key deserializer
    StreamsConfig.KEY_SERDE_CLASS_CONFIG -> Serdes.String.getClass.getName,
    // value deserializer
    StreamsConfig.VALUE_SERDE_CLASS_CONFIG -> classOf[GenericAvroSerde].getName,

    ConsumerConfig.GROUP_ID_CONFIG -> "group-foo",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
    "schema.registry.url" -> "http://localhost:8081"
  //    "producer.interceptor.classes" -> "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor",
  //    "consumer.interceptor.classes" -> "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor"
  )

  def config(applicationId: String): Properties = {
    import scala.collection.JavaConverters._
    val settings = new Properties
    settings.putAll(configAsMap(applicationId).asJava)
    settings
  }
}
