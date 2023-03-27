#!/bin/bash

# Adds a secret needed to access Vault.
# The VAULT_TOKEN secret entry is used by set-k8s-secrets.sh.
# The personal Vault token is copied from the Vault web GUI and expires monthly.

if [ -z "$1" ]; then
  echo "Usage: $0 \"<paste token from Vault web GUI>\""
  exit 1
fi

export VAULT_TOKEN_BASE64=$(echo -n "$1" | base64)
# The runner is deployed to the dev environment, so this secret is only needed there.
scripts/echo-secret-yaml.sh "vro-vault" "  VAULT_TOKEN: $VAULT_TOKEN_BASE64" | \
  kubectl -n "va-abd-rrd-dev" replace --force -f -
