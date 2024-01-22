#!/bin/bash

# Define the service name
service_name="rabbitmq-service"

# Stop the RabbitMQ service using Docker Compose
echo "Stopping the $service_name service using Docker Compose..."
docker-compose stop $service_name

# Remove all unused volumes (caution: this will delete data in all unused volumes)
echo "Pruning unused Docker volumes..."
docker volume prune -f

# Restart the RabbitMQ service using Docker Compose
echo "Restarting the $service_name service..."
docker-compose up -d $service_name

echo "Script completed."
