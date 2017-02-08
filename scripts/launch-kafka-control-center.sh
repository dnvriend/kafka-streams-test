#!/bin/bash
source common.sh
echo "control center is running on port: 9021"
$KAFKA_HOME/bin/control-center-start ./control-center.properties