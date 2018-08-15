#!/usr/bin/env bash

sbt test pack

collector/deploy.sh

processor/deploy.sh

frontend/deploy.sh

remoteDir=/home/anthony/projects/paris-velib
remoteHost="anthony@192.168.1.26"
portNumber=222

rsync -avrc --delete \
    --exclude tmp \
    --exclude-from ./deploy.sh \
    -e "ssh -p $portNumber" \
    scripts "$remoteHost":"$remoteDir"