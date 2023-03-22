# Creates image for self-hosted GitHub runner, which is used to access the VA's LHDI Hashicorp Vault.
# When deployed in LHDI, the runner runs in the VA's network, which is required to access Vault.
# To publish or start this runner, use GitHub Action workflow deploy-secrets.yml

FROM ghcr.io/department-of-veterans-affairs/lighthouse-di-github-runner-base-image/runner-base-image:0.1.24

USER root

# gettext-base provides envsubst needed by deploy-secrets.yml
# gpg is needed for vault, which is useful for testing access to Vault
# Vault install instructions: https://www.hashicorp.com/official-packaging-guide
RUN apt-get update -y && \
    apt-get install gettext-base wget gpg -y --no-install-recommends && \
    wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor | tee /usr/share/keyrings/hashicorp-archive-keyring.gpg && \
    gpg --no-default-keyring --keyring /usr/share/keyrings/hashicorp-archive-keyring.gpg --fingerprint && \
    echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | \
    tee /etc/apt/sources.list.d/hashicorp.list && \
    apt update && apt install vault &&  \
    apt-get clean

COPY set-k8s-secrets.sh .

USER docker
