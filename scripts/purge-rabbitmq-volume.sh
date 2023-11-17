#!/bin/bash

# Define the volume name
volume_name="vro_var_rabbitmq"

# Get container IDs using the volume
container_ids=$(docker ps -q --filter volume=$volume_name)

# Check if any container IDs were found
if [ -z "$container_ids" ]; then
    echo "No container found using volume $volume_name."
else
    # Iterate over each container ID
    for id in $container_ids; do
        # Get container name
        container_name=$(docker inspect --format='{{.Name}}' $id | sed 's/^\/\+//')

        # Print container name
        echo "Forcefully stopping container $container_name ($id) using volume $volume_name..."

        # Forcefully stop the container
        docker kill $id
    done
fi

# Remove the volume
echo "Removing volume $volume_name..."
docker volume rm $volume_name

# Optionally, recreate the volume
echo "Recreating volume $volume_name..."
docker volume create $volume_name

echo "Script completed."
