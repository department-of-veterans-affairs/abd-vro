#!/bin/bash
set -e

# Check if the environment variables are set
if [ -z "${RABBITMQ_USER}" ] || [ -z "${RABBITMQ_PASSWORD}" ]; then
    echo "Required environment variables are missing."
    exit 1
fi

# Update rabbitmq.conf with environment variable values
echo "default_user = ${RABBITMQ_USER}" >> /etc/rabbitmq/rabbitmq.conf
echo "default_pass = ${RABBITMQ_PASSWORD}" >> /etc/rabbitmq/rabbitmq.conf


# Start RabbitMQ
exec /opt/bitnami/scripts/rabbitmq/entrypoint.sh /opt/bitnami/scripts/rabbitmq/run.sh
