package com.github.dnvriend

import java.util.Properties

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig

// see: http://docs.confluent.io/3.1.2/streams/developer-guide.html#overview
object KafkaStreamingConfig {
  def configAsMap(applicationId: String) = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> applicationId,
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.ZOOKEEPER_CONNECT_CONFIG -> "localhost:2181",
    StreamsConfig.KEY_SERDE_CLASS_CONFIG -> Serdes.ByteArray.getClass.getName,
    StreamsConfig.VALUE_SERDE_CLASS_CONFIG -> classOf[GenericAvroSerde],
    AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "http://localhost:8081",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )

  def config(applicationId: String): Properties = {
    import scala.collection.JavaConverters._
    val settings = new Properties
    settings.putAll(configAsMap(applicationId).asJava)
    settings
  }
}
