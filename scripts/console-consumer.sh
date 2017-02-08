#!/bin/bash
source common.sh
$KAFKA_HOME/bin/kafka-console-consumer --topic $1 \
         --bootstrap-server localhost:9092 \
         --from-beginning