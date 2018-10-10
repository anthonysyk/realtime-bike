#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/paris-velib/collector
remoteHost=$1

rsync -avrc \
    --exclude tmp \
    --exclude-from ./deploy.sh \
    -e "ssh" \
    target/pack/ "$remoteHost":"$remoteDir"