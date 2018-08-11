#!/usr/bin/env bash

KAFKA_HOME=/Users/versatileflow/tools/confluent-4.1.0

$KAFKA_HOME/bin/confluent stop

wait

$KAFKA_HOME/bin/confluent destroy

wait

$KAFKA_HOME/bin/confluent start

wait

./src/main/bin/create-topics.sh
