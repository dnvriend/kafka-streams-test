#!/bin/bash
source common.sh

$KAFKA_HOME/bin/connect-standalone ./kafka-connect-avro-standalone.properties ./kafka-connect-file-sink.properties