#!/bin/bash

# Define the volume name
volume_name="vro_var_rabbitmq"

# Find all containers using the volume
container_ids=$(docker ps -a --filter volume=$volume_name -q)

# Check if any container IDs were found and stop them
if [ -n "$container_ids" ]; then
    echo "Stopping containers using volume $volume_name..."
    docker stop $container_ids

    echo "Waiting for containers to fully stop..."
    sleep 10  # Wait for a few seconds to ensure containers are fully stopped
else
    echo "No container found using volume $volume_name."
fi

# Remove the volume
echo "Removing volume $volume_name..."
if docker volume rm $volume_name; then
    echo "Volume $volume_name removed successfully."
else
    echo "Failed to remove volume $volume_name. It might still be in use."
    exit 1
fi

# Optionally, recreate the volume
echo "Recreating volume $volume_name..."
docker volume create $volume_name

echo "Script completed."
