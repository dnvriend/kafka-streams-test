#!/bin/bash
source common.sh
#./bin/kafka-avro-console-consumer --topic test \
#         --zookeeper localhost:2181 \
#         --from-beginning

$KAFKA_HOME/bin/kafka-console-consumer --topic test \
         --bootstrap-server localhost:9092 \
         --from-beginning