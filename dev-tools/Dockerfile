FROM bitnami/minideb:bookworm

# hadolint ignore=DL3018,DL3009
RUN apt-get update && \
    install_packages curl redis sudo && \
    install_packages postgresql-client

RUN adduser --no-create-home --disabled-password tron

USER tron

# This will cause the container to sleep for 10 hours before automatically terminating.
# This will prevent the container from lingering in our LHDI environments
ENTRYPOINT ["/bin/sleep", "36000"]
