#!/bin/bash
source common.sh
$KAFKA_HOME/bin/kafka-avro-console-consumer \
         --topic $1 \
         --bootstrap-server localhost:9092 \
         --property print.key=true \
         --from-beginning