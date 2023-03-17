# Creates image for self-hosted GitHub runner, which is used to access the VA's LHDI Hashicorp Vault.
# When deployed in LHDI, the runner runs in the VA's network, which is required to access Vault.

# docker build -f Dockerfile -t department-of-veterans-affairs/abd-vro-internal/vro-ghrunner .
# docker push ghcr.io/department-of-veterans-affairs/abd-vro-internal/vro-ghrunner

FROM ghcr.io/department-of-veterans-affairs/lighthouse-di-github-runner-base-image/runner-base-image:0.1.24
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro-internal

USER root

# gettext-base provides envsubst needed by deploy-secrets.yml
RUN apt-get update -y && \
    apt-get install gettext-base -y --no-install-recommends && \
    apt-get clean

USER docker
