#!/usr/bin/env bash

docker run -v /mnt/cassandra:/var/lib/cassandra -d --restart always -p 9042:9042 -p 9160:9160 --name cassandra versatile/cassandra
