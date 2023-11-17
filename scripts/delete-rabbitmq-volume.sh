#!/bin/bash

# Define the volume name
volume_name="vro_var_rabbitmq"

# Find the container ID that is using the volume
container_id=$(docker ps -a --filter volume=$volume_name -q)

# Check if a container ID was found
if [ -n "$container_id" ]; then
    echo "Container using the volume found: $container_id"

    # Stop the container
    echo "Stopping container..."
    docker stop "$container_id"

    # Remove the container
    echo "Removing container..."
    docker rm "$container_id"
else
    echo "No container is using the volume '$volume_name'."
fi

# Remove the volume
echo "Removing the volume '$volume_name'..."
docker volume rm $volume_name || echo "Volume '$volume_name' could not be removed or does not exist."
