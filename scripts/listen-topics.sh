#!/usr/bin/env bash

export PATH="$PATH:/opt/confluent-5.0.0/bin"

TOPIC=$1

kafka-console-consumer --topic $TOPIC --bootstrap-server localhost:9092 --from-beginning
