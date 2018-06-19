#!/usr/bin/env bash


$PATH/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic Station

$PATH/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic StationLogs

$PATH/kafka-topics --list --zookeeper localhost:2181

