FROM bitnami/kafka:3.3

# Settings are for development only
# ENV ALLOW_PLAINTEXT_LISTENER=yes
#ENV KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092

# "Security" section of https://hub.docker.com/r/bitnami/kafka/
ENV KAFKA_TLS_TYPE=PEM
ENV KAFKA_CERTIFICATE_PASSWORD=mockCertificate
ENV KAFKA_TLS_TRUSTSTORE_FILE=/Users/yoom/dev/abd-vro/certificate.p12

# Don't verify host name of SSL certificates
ENV KAFKA_CFG_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=""

# Client config:
#ENV KAFKA_CFG_LISTENERS=SASL_SSL://:9092,CONTROLLER://:9093
#ENV KAFKA_CFG_ADVERTISED_LISTENERS=SASL_SSL://localhost:9092
#ENV KAFKA_CLIENT_USERS=user
#ENV KAFKA_CLIENT_PASSWORDS=password


#      KAFKA_INTER_BROKER_LISTENER_NAME: TLS #cannot use SSL as name, due to bug in configure script

#      KAFKA_SSL_CLIENT_AUTH: "required"
#      KAFKA_SSL_KEYSTORE_LOCATION: /etc/kafka/secrets/broker-keypair.pem
#      KAFKA_SSL_KEY_PASSWORD: codingharbour
#      KAFKA_SSL_KEYSTORE_TYPE: PEM
#      KAFKA_SSL_TRUSTSTORE_LOCATION: /etc/kafka/secrets/root.crt
#      KAFKA_SSL_TRUSTSTORE_TYPE: PEM

# To build: ./gradlew -p mocks :mock-bie-kafka:docker
# Start container: docker run --rm -it -p 9092:9092 va/vro_mocks-mock-bie-kafka
# To test: https://docs.confluent.io/platform/current/clients/kafkacat-usage.html
#   kcat -b 127.0.0.1:9092 -P -t topic1
#   kcat -b 127.0.0.1:9092 -C -t topic1
# Note: use 127.0.0.1 (instead of `localhost`) in case /etc/hosts causes `localhost` to resolve to `::1`
#   - https://github.com/edenhill/kcat/issues/16#issuecomment-66664574
#   - https://www.confluent.io/blog/kafka-client-cannot-connect-to-broker-on-aws-on-docker-etc/

# To run in Docker Compose setup: COMPOSE_PROFILES="kafka" ./gradlew -p mocks :dockerComposeUp
# Start the kafka client in the Docker Compose network and connect to `mock-bie-kafka:9092`
