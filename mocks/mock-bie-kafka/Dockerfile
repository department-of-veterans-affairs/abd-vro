FROM bitnami/kafka:3.4

# Settings are for development only
ENV ALLOW_PLAINTEXT_LISTENER=yes
ENV KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092

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
