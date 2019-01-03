#!/usr/bin/env bash

curl -d '{
  "name": "cassandra-sink-station-structured",
  "config": {
    "connector.class": "com.datamountaineer.streamreactor.connect.cassandra.sink.CassandraSinkConnector",
    "tasks.max": 1,
    "topics": "station_structured",
    "connect.cassandra.kcql": "INSERT INTO station_structured_topic SELECT * FROM station_structured",
    "connect.cassandra.port": 9042,
    "connect.cassandra.key.space": "rtbike",
    "connect.cassandra.contact.points": "localhost",
    "connect.cassandra.username": "versatile",
    "connect.cassandra.password": "versatile75!",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url":"http://localhost:8081",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url":"http://localhost:8081"
  }
}
' -H "Content-Type: application/json" -X POST http://51.15.87.1:8083/connectors

curl -d '{
  "name": "cassandra-sink-station-raw",
  "config": {
    "connector.class": "com.datamountaineer.streamreactor.connect.cassandra.sink.CassandraSinkConnector",
    "tasks.max": 1,
    "topics": "station_raw",
    "connect.cassandra.kcql": "INSERT INTO station_raw_topic SELECT * FROM station_raw",
    "connect.cassandra.port": 9042,
    "connect.cassandra.key.space": "rtbike",
    "connect.cassandra.contact.points": "localhost",
    "connect.cassandra.username": "versatile",
    "connect.cassandra.password": "versatile75!",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url":"http://localhost:8081",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url":"http://localhost:8081"
  }
}
' -H "Content-Type: application/json" -X POST http://51.15.87.1:8083/connectors

