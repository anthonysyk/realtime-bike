#!/usr/bin/env bash

#!/usr/bin/env bash

remoteDir=/home/anthony/projects/paris-velib/processor
port=9001

docker rmi paris-velib/processor

cd "$remoteDir" && docker build -t paris-velib/processor:latest .

docker container rm -f paris-velib-processor

docker run -p "$port:$port" -d --restart always --name=paris-velib-processor paris-velib/processor
