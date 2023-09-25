# Use the official Confluent Schema Registry image as the base
FROM confluentinc/cp-schema-registry:latest

USER root

# Install jq using yum
RUN yum install -y jq && \
    yum clean all
