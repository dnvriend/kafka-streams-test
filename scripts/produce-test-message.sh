#!/bin/bash
source common.sh
$KAFKA_HOME/bin/kafka-avro-console-producer \
         --broker-list localhost:9092 --topic test \
         --property log.message.timestamp.type CreateTime \
         --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'