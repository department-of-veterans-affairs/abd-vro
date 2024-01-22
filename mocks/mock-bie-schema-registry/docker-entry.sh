#!/bin/bash

# Start Schema Registry in the background
/etc/confluent/docker/run &

# Define the Schema Registry URL
SCHEMA_REGISTRY_URL="http://localhost:8081"

# Wait for Schema Registry to be available
until curl --silent --output /dev/null --fail "$SCHEMA_REGISTRY_URL"; do
  echo "Waiting for Schema Registry to be available..."
  sleep 5
done


# Navigate to /avro/events folder
cd /avro/events

# Loop through all .avsc files in the folder
for file in *.avsc; do
  # Extract the filename without extension to be used as subject name
  subject="${file%.avsc}"

  # Read the file content as schema data
  schema=$(cat "$file")

  # Register the schema with the Schema Registry
  echo "Registering schema for subject: $subject"
  curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data "$(jq -n --arg schema "$schema" '{"schema": $schema}')" \
    "$SCHEMA_REGISTRY_URL/subjects/$subject/versions"
done


# Wait for Schema Registry process to complete
wait $!
