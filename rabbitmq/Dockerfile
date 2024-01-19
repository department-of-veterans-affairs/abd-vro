# Use the Bitnami RabbitMQ image as the base
FROM bitnami/rabbitmq:3.12

# Copy the modified configuration file to the correct location
COPY rabbitmq.conf /opt/bitnami/rabbitmq/etc/rabbitmq/rabbitmq.conf

## Copy the entrypoint script
#COPY --chmod=755 entrypoint.sh /entrypoint.sh

# Set the script as the entrypoint
#ENTRYPOINT ["/entrypoint.sh"]
