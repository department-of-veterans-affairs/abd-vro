# Use Amazon AWS CLI image as the base image
FROM amazon/aws-cli:2.16.5

# Use bitnami/minideb:bookworm as a second stage to install PostgreSQL client
FROM bitnami/minideb:bookworm

# Set SHELL option to handle pipes more safely
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# hadolint ignore=DL3018,DL3009
RUN apt-get update && \
    install_packages sudo curl unzip gnupg ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Copy AWS CLI from the first stage
COPY --from=amazon/aws-cli:latest /usr/local/aws-cli /usr/local/aws-cli

# Set AWS CLI binary to the PATH
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
