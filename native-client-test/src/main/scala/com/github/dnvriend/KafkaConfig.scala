package com.github.dnvriend

import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.ProducerConfig

// see: http://docs.confluent.io/3.1.2/streams/developer-guide.html#overview
object KafkaConfig {
  def configAsMap = Map(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    "schema.registry.url" -> "http://localhost:8081"
  )

  def config(): Properties = {
    import scala.collection.JavaConverters._
    val settings = new Properties
    settings.putAll(configAsMap.asJava)
    settings
  }
}
