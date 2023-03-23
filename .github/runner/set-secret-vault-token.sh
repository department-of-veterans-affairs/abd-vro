#!/bin/bash

# Adds a secret needed to access Vault.
# The VAULT_TOKEN is copied from the Vault web GUI and expires monthly.

if [ -z "$1" ]; then
  echo "Usage:
   VAULT_TOKEN=<paste token from Vault web GUI>
   $0 \"\$VAULT_TOKEN\"
  "
  exit 1
fi

: ${VAULT_TOKEN:=$1}

# Originates from the Vault web GUI
[ "$VAULT_TOKEN" ] || { echo "Missing VAULT_TOKEN"; exit 3; }

dumpYaml(){
  # Do not modify indentation
  echo "
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: $1
data:
$2
"
}

export VAULT_TOKEN_BASE64=$(echo -n "$VAULT_TOKEN" | base64)
# The runner is deployed to the dev environment, so this secret is only needed there.
dumpYaml "vro-vault" "  VAULT_TOKEN: $VAULT_TOKEN_BASE64" | \
  kubectl -n "va-abd-rrd-dev" apply -f -
