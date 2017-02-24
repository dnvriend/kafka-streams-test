#!/bin/bash
source common.sh
$KAFKA_HOME/bin/kafka-console-producer \
         --broker-list localhost:9092 \
         --topic $1 \
         --property log.message.timestamp.type CreateTime
