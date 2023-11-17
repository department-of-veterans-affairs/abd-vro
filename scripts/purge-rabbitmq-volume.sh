#!/bin/bash

# Define the service and volume names
service_name="rabbitmq-service"
volume_name="vro_var_rabbitmq"

# Stop the RabbitMQ service using Docker Compose
echo "Stopping the $service_name service using Docker Compose..."
docker-compose stop $service_name

# Remove the existing Docker volume (caution: this will delete all data in the volume)
echo "Removing volume $volume_name..."
docker volume rm $volume_name

# Recreate the Docker volume
echo "Recreating volume $volume_name..."
docker volume create $volume_name

# Restart the RabbitMQ service using Docker Compose
echo "Restarting the $service_name service..."
docker-compose up -d $service_name

echo "Script completed."
