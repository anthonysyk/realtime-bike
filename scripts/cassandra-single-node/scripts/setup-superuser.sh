#!/usr/bin/env bash

cqlsh -u cassandra -p cassandra -f /home/setup/1-set-replication.sql

nodetool repair system_auth

cqlsh -u cassandra -p cassandra -f /home/setup/2-create-new-user.sql

cqlsh -u versatile -p versatile75! -f /home/setup/3-remove-default-user.sql
