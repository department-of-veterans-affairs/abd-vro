# Use the official Confluent Schema Registry image as the base
FROM confluentinc/cp-schema-registry:7.5.1

USER root

# Install jq using yum (as root)
RUN yum install -y jq-1.6 && yum clean all

# Create a new user 'tron' and create a home directory for the user
RUN useradd -m tron

# Switch to the new user 'tron'
USER tron
