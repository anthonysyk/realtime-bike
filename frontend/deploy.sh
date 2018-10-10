#!/usr/bin/env bash

cd "${0%/*}"

remoteDir=/home/anthony/projects/paris-velib/frontend
remoteHost=$1

		rsync -avrc \
		    --exclude 'deploy.sh' \
		    --exclude 'node_modules/' \
		    -e "ssh" \
			. "$remoteHost":"$remoteDir"
