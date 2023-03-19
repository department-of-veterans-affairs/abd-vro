#!/bin/bash

# input
TARGET_ENV=
KUBE_CONFIG=
# These are the env variable names, as well as part of the Vault path
VRO_SECRETS_NAMES="VRO_SECRETS_APP VRO_SECRETS_BIP VRO_SECRETS_LH"
SERVICE_NAMES="postgres redis ..."

setupKubeConfig(){
  echo -n "${KUBE_CONFIG}" | base64 -d > ~/.kube/config
  chmod go-rwx ~/.kube/config
}
setupKubeConfig

queryVault(){
  # TODO
  query "deploy/${{input.target_env}}/$1" -o json
}

dumpYaml(){
  # Do not modify indentation
  echo "
apiVersion: v1
kind: Secret
immutable: true
type: Opaque
metadata:
  name: $1
data:
$2
"
}

splitSecretData(){
  ENV_VARS=$(echo "$1" | jq -r 'to_entries | .[] | "\(.key) \(.value|@sh)"')
  echo "$ENV_VARS" | while read K V; do
    echo "  $K: $(echo -n "$V" | base64 -w0)"
  done
}
addCmdAsSecretData(){
  EXPORT_CMDS=$(echo "$2" | jq -r 'to_entries | .[] | "export \(.key)=\(.value|@sh)"' || exit 3)
  # Encode EXPORT_CMDS b/c it's multiline, then encode it again for the yaml file
  echo "  $1: $(echo "$EXPORT_CMDS" | base64 -w0 | base64 -w0)"
}
collectSecretData(){
  for VRO_SECRETS in "$@"; do
    JSON=$(queryVault "$VRO_SECRETS")
    echo "$(addCmdAsSecretData "$VRO_SECRETS" "$JSON")"
  done
}

# For each SERVICE_NAMES, set a SECRET_NAME secret, where
# each key-value pair has a single value (a normal secret).
# These secrets are used for third-party containers that expect environment variables to be set.
for SERVICE_NAME in $SERVICE_NAMES; do
  JSON=$(queryVault "$SERVICE_NAME")
  SERVICE_SECRET_DATA=$(splitSecretData "$JSON")
  dumpYaml "$SERVICE_NAME" "$SERVICE_SECRET_DATA" | \
    kubectl -n "va-abd-rrd-${TARGET_ENV}" apply -f -
done

# Set the `vro-secrets` secret, where for each key-value pair,
# the key begins with `VRO_SECRETS_` and the value is a multiline string consisting
# of a series of `export VAR1=VAL1` lines. These multiline strings will be interpreted
# and evaluated by set-env.src in each VRO container.
# New environment variables can be added to these secrets without modifying
# Helm configurations -- simply add them to Vault.
SECRET_DATA=$(collectSecretData $VRO_SECRETS_NAMES)
dumpYaml vro-secrets "$SECRET_DATA" | \
  kubectl -n "va-abd-rrd-${TARGET_ENV}" apply -f -

# TODO: Once all relevant pods are up or after some time, delete the secrets.
# Or use preStop hook to delete those secrets on pod shutdown.
# But restarted pods will fail b/c secrets aren't available.
