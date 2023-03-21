# Creates image for self-hosted GitHub runner, which is used to access the VA's LHDI Hashicorp Vault.
# When deployed in LHDI, the runner runs in the VA's network, which is required to access Vault.
# To publish or start the runner, use GitHub Action workflow gh-runner.yml

FROM ghcr.io/department-of-veterans-affairs/lighthouse-di-github-runner-base-image/runner-base-image:0.1.24
#LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd-vro-internal

USER root

# gettext-base provides envsubst needed by deploy-secrets.yml
RUN apt-get update -y && \
    apt-get install gettext-base -y --no-install-recommends && \
    apt-get clean

USER docker
