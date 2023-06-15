FROM bitnami/kafka:latest

# Settings are for development only
ENV ALLOW_PLAINTEXT_LISTENER=yes
ENV KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092

# To test: docker run --rm -it -p 9092:9092 va/abd_vro-mock-bie-kafka
# or docker run -it --env ALLOW_PLAINTEXT_LISTENER=yes --env "KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092" va/abd_vro-mock-bie-kafka
# Then: https://docs.confluent.io/platform/current/clients/kafkacat-usage.html
# kcat -b 127.0.0.1:9092 -P -t hi
# kcat -b 127.0.0.1:9092 -C -t hi
# use 127.0.0.1 b/c /etc/hosts can cause localhost to resolve to ::1
#   https://github.com/edenhill/kcat/issues/16#issuecomment-66664574
#   https://www.confluent.io/blog/kafka-client-cannot-connect-to-broker-on-aws-on-docker-etc/
