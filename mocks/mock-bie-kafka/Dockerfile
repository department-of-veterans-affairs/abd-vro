# BIE's Kafka version is 3.3.x
FROM bitnami/kafka:3.3

# To build: ./gradlew -p mocks :mock-bie-kafka:docker
# Start container: docker run --rm -it -p 9094:9094 va/vro_mocks-mock-bie-kafka
# To test: https://docs.confluent.io/platform/current/clients/kafkacat-usage.html
#   kcat -b 127.0.0.1:9094 -P -t topic1
#   kcat -b 127.0.0.1:9094 -C -t topic1
# Note: use 127.0.0.1 (instead of `localhost`) in case /etc/hosts causes `localhost` to resolve to `::1`
#   - https://github.com/edenhill/kcat/issues/16#issuecomment-66664574
#   - https://www.confluent.io/blog/kafka-client-cannot-connect-to-broker-on-aws-on-docker-etc/

# To run in Docker Compose setup: COMPOSE_PROFILES="kafka" ./gradlew -p mocks :dockerComposeUp
# Start the kafka client in the Docker Compose network and connect to `mock-bie-kafka:9094`

## For debugging this container, uncomment the following:
#ENTRYPOINT ["tail", "-f", "/dev/null"]
# Then run:
#   COMPOSE_PROFILES="kafka" ./gradlew :dockerComposeUp
#   docker-compose exec mock-bie-kafka bash
# And at the shell prompt:
#   /opt/bitnami/scripts/kafka/entrypoint.sh /opt/bitnami/scripts/kafka/run.sh
