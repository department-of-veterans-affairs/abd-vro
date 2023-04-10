#!/usr/bin/env bash

function docker_container_wait {
until [ "$(docker inspect -f '{{.State.Health.Status}}' "vro-app-1")" == "healthy" ]; do
    echo "Waiting for Docker container to be available..."
    sleep 1
done
}

docker_container_wait
curl http://localhost:8080/v3/api-docs > openapi.json
sed -i '1s/^.\{4\}//;s/^M//g' openapi.json
spectral lint openapi.json
