#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/paris-velib/frontend
remoteHost="anthony@192.168.1.26"
portNumber=222

		rsync -avrc \
		    --exclude 'deploy.sh' \
		    --exclude 'node_modules/' \
		    -e "ssh -p $portNumber" \
			. "$remoteHost":"$remoteDir"
