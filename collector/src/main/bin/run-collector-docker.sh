#!/usr/bin/env bash

remoteDir=/home/anthony/projects/paris-velib/collector

docker rmi paris-velib/collector

cd "$remoteDir" && docker build -t paris-velib/collector:latest .

docker container rm -f paris-velib-collector

docker run -d --restart always --name=paris-velib-collector paris-velib/collector
