# Use the Bitnami RabbitMQ image as the base
FROM bitnami/rabbitmq:3.12

# Copy the modified configuration file to the correct location
COPY --chmod=755 rabbitmq.conf /opt/bitnami/rabbitmq/etc/rabbitmq/rabbitmq.conf

# Copy the entrypoint script
COPY --chmod=755 entrypoint.sh /entrypoint.sh

# Temporarily switch to root to adjust file permissions
USER root
RUN chmod a+rw /opt/bitnami/rabbitmq/etc/rabbitmq/rabbitmq.conf

# Switch back to the non-root user (if applicable)
USER 100

# Set the script as the entrypoint
ENTRYPOINT ["/entrypoint.sh"]
