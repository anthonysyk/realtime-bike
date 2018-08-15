#!/usr/bin/env bash

remoteDir=/home/anthony/projects/paris-velib/frontend
port=4000

docker rmi paris-velib/frontend

cd "$remoteDir" && docker build -t paris-velib/frontend:latest .

docker container rm -f paris-velib-frontend

docker run -p "$port:$port" --network="host" -d --restart always --name=paris-velib-frontend paris-velib/frontend

docker images -q | xargs docker rmi
