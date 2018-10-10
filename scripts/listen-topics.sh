#!/usr/bin/env bash

PATH=/opt/confluent-5.0.0/bin

$PATH/kafka-console-consumer --topic StationLogs --bootstrap-server localhost:9092 --from-beginning
