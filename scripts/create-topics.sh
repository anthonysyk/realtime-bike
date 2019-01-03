#!/usr/bin/env bash

### -1 = "retains forever"
### retention.ms has higher priority than retention.minutes

export PATH="$PATH:/opt/confluent/bin"

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic replay_station --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_raw --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_structured --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic station_state --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_5min --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_15min --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_30min --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_1h --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_3h --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_12h --config retention.ms=-1

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic window_1j --config retention.ms=-1

kafka-topics --list --zookeeper localhost:2181

