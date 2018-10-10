#!/usr/bin/env bash

remoteHost="root@51.15.87.1"

sbt pack

collector/deploy.sh $remoteHost

processor/deploy.sh $remoteHost

frontend/deploy.sh $remoteHost

remoteDir=/home/anthony/projects/paris-velib

rsync -avrc --delete \
    --exclude tmp \
    --exclude-from ./deploy.sh \
    -e "ssh" \
    scripts "$remoteHost":"$remoteDir"