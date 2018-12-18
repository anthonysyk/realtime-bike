#!/usr/bin/env bash

export PATH="$PATH:/opt/confluent/bin"

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_raw --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_structured --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_state --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_5min --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_15min --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_30min --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_1h --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_3h --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_12h --config delete.retention.ms=0

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_1j --config delete.retention.ms=0

kafka-topics --list --zookeeper localhost:2181

