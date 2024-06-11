FROM bitnami/minideb:bookworm

# Set SHELL option to handle pipes more safely
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# hadolint ignore=DL3018,DL3009
RUN apt-get update && \
    install_packages sudo curl unzip gnupg ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Install AWS CLI
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip aws

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
