#!/usr/bin/env bash

sbt pack

collector/deploy-old.sh

processor/deploy-old.sh

frontend/deploy-old.sh

remoteDir=/home/anthony/projects/paris-velib
remoteHost="anthony@192.168.1.26"
portNumber=222

rsync -avrc --delete \
    --exclude tmp \
    --exclude-from ./deploy.sh \
    -e "ssh -p $portNumber" \
    scripts "$remoteHost":"$remoteDir"