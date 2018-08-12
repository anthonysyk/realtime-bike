#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/paris-velib/collector
remoteHost="anthony@192.168.1.26"
portNumber=222

rsync -avrc \
    --exclude tmp \
    --exclude-from ./deploy.sh \
    -e "ssh -p $portNumber" \
    target/pack/ "$remoteHost":"$remoteDir"