#!/bin/bash
source common.sh
$KAFKA_HOME/bin/kafka-avro-console-producer \
         --broker-list localhost:9092 --topic Updates \
         --property log.message.timestamp.type CreateTime \
         --property value.schema='{"type":"record","name":"Update","fields":[{"name":"name","type":"string"}, {"name":"count","type":"int"}]}'