# Use the Bitnami RabbitMQ image as the base
FROM bitnami/rabbitmq:3.12

COPY rabbitmq.conf /tmp/rabbitmq.conf
COPY definitions.json /tmp/definitions.json

USER root

RUN chown 1001 /tmp/rabbitmq.conf  \
    && chmod g+w /tmp/rabbitmq.conf

USER 1001

## Copy the entrypoint script
COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
