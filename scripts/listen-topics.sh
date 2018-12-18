#!/usr/bin/env bash

export PATH="$PATH:/opt/confluent/bin"

TOPIC=$1

kafka-console-consumer --topic $TOPIC --bootstrap-server localhost:9092 --from-beginning
