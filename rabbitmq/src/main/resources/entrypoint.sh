#!/bin/bash
set -e

# Check if RABBITMQ_USERNAME or RABBITMQ_USER is set and assign it to a variable
if [ ! -z "$RABBITMQ_USERNAME" ]; then
    RABBITMQ_USER_VALUE="$RABBITMQ_USERNAME"
elif [ ! -z "$RABBITMQ_USER" ]; then
    RABBITMQ_USER_VALUE="$RABBITMQ_USER"
else
    echo "Neither RABBITMQ_USERNAME nor RABBITMQ_USER is set. Exiting."
    exit 1
fi

# Define the path to your configuration file
CONFIG_FILE="/tmp/rabbitmq.conf"

# Replace lines in the config file
sed -i "s/^default_user .*/default_user = $RABBITMQ_USER_VALUE/" $CONFIG_FILE
sed -i "s/^default_pass .*/default_pass = ${RABBITMQ_PASSWORD}/" $CONFIG_FILE


cp /tmp/rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
cp /tmp/definitions.json /etc/rabbitmq/definitions.json

exec /opt/bitnami/scripts/rabbitmq/entrypoint.sh /opt/bitnami/scripts/rabbitmq/run.sh
