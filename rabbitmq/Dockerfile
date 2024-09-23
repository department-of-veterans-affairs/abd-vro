# Use the Bitnami RabbitMQ image as the base
FROM bitnami/rabbitmq:4.0

COPY rabbitmq.conf /tmp/rabbitmq.conf
COPY definitions.json /tmp/definitions.json

USER root

# Upgrade libssl3 and openssl to the specific version
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       libssl3=3.0.14-1~deb12u2 \
       openssl=3.0.14-1~deb12u2 \
    && rm -rf /var/lib/apt/lists/*

# Change ownership and permissions of configuration files
RUN chown 1001 /tmp/rabbitmq.conf \
    && chmod g+w /tmp/rabbitmq.conf

RUN chown 1001 /tmp/definitions.json \
    && chmod g+w /tmp/definitions.json

USER 1001

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
