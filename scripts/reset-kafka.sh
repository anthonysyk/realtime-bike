#!/usr/bin/env bash

KAFKA_HOME=/opt/confluent-4.1.0

$KAFKA_HOME/bin/confluent stop

wait

$KAFKA_HOME/bin/confluent destroy

wait

$KAFKA_HOME/bin/confluent start

wait

create-topics.sh