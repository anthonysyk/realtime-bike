#!/usr/bin/env bash

KAFKA_HOME=/Users/versatileflow/tools/confluent-4.1.0


$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Station

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic StationLogs

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic State

$KAFKA_HOME/bin/kafka-topics --list --zookeeper localhost:2181

