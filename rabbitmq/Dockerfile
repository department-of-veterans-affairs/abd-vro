# Use the Bitnami RabbitMQ image as the base
FROM bitnami/rabbitmq:3.12

USER root

# Create a new user 'tron' and create a home directory for the user
RUN useradd -m tron

# Copy the modified configuration file to the correct location
COPY rabbitmq.conf /tmp/rabbitmq.conf

RUN chmod +x /tmp/rabbitmq.conf

## Copy the entrypoint script
COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
