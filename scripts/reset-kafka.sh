#!/usr/bin/env bash

KAFKA_HOME=/opt/confluent

$KAFKA_HOME/bin/confluent stop

wait

$KAFKA_HOME/bin/confluent destroy

wait

$KAFKA_HOME/bin/confluent start

wait

./create-topics.sh
