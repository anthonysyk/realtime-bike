#!/usr/bin/env bash

KAFKA_HOME=/opt/confluent-4.1.0


$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Station --config retention.ms=86400000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic StationLogs --config retention.ms=86400000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic State --config retention.ms=259200000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Window1min --config retention.ms=259200000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Window5min --config retention.ms=259200000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Window30min --config retention.ms=259200000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Window1h --config retention.ms=604800000

$KAFKA_HOME/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic Window3h --config retention.ms=604800000

$KAFKA_HOME/bin/kafka-topics --list --zookeeper localhost:2181

