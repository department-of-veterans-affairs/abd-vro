# Use the official AWS CLI image for the first stage
FROM amazon/aws-cli:2.17.48 as awscli

# Use the bitnami/minideb:bookworm as the base image for the second stage
FROM bitnami/minideb:bookworm

# Set SHELL option to handle pipes more safely
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# hadolint ignore=DL3018,DL3009
RUN apt-get update && \
    install_packages sudo curl unzip gnupg ca-certificates groff && \
    rm -rf /var/lib/apt/lists/*

# Copy AWS CLI from the awscli stage
COPY --from=awscli /usr/local/aws-cli /usr/local/aws-cli

# Make the AWS CLI binaries available in PATH
ENV PATH="/usr/local/aws-cli/v2/current/bin:${PATH}"

# Download and import PostgreSQL GPG key
RUN curl -s https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

# Add PostgreSQL repository and install the latest PostgreSQL client
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ bookworm-pgdg main" > /etc/apt/sources.list.d/pgdg.list && \
    apt-get update && \
    install_packages postgresql-client-16 && \
    rm -rf /var/lib/apt/lists/*

# Add user 'tron'
RUN adduser --disabled-password tron

USER tron

# This will cause the container to sleep for 10 hours before automatically terminating.
# This will prevent the container from lingering in our LHDI environments
ENTRYPOINT ["/bin/sleep", "36000"]
