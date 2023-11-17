#!/bin/bash

# Define the volume name
volume_name="vro_var_rabbitmq"

# Find the container ID using the volume
container_id=$(docker ps -a --filter volume=$volume_name -q)

# Check if a container ID was found and stop it
if [ -n "$container_id" ]; then
    echo "Stopping container $container_id using volume $volume_name..."
    docker stop "$container_id"
else
    echo "No container found using volume $volume_name."
fi

# Remove the volume
echo "Removing volume $volume_name..."
docker volume rm $volume_name

# Optionally, recreate the volume
echo "Recreating volume $volume_name..."
docker volume create $volume_name

# Optionally, restart the container
if [ -n "$container_id" ]; then
    echo "Restarting container $container_id..."
    docker start "$container_id"
fi

echo "Script completed."
