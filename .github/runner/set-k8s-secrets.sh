#!/bin/bash
# This script runs in the gh-runner container to set K8s secrets needed by VRO containers.

NAMESPACE=$(cat /run/secrets/kubernetes.io/serviceaccount/namespace)
: ${TARGET_ENV:=${NAMESPACE#va-abd-rrd-}}
echo "TARGET_ENV=$TARGET_ENV"

if [ "$KUBE_CONFIG" ]; then
  # Enable setting secrets in either cluster
  mkdir -p ~/.kube
  echo -n "${KUBE_CONFIG}" > ~/.kube/config
  chmod go-rwx ~/.kube/config
else
  echo "Missing KUBE_CONFIG. Using kubectl namespace of container."
fi

# Test $KUBE_CONFIG
kubectl -n "va-abd-rrd-${TARGET_ENV}" get pods || {
  echo "ERROR: Kubernetes access token (KUBE_CONFIG) may have expired. Run scripts/set-secret-kube-config.sh locally."
  echo "See https://github.com/department-of-veterans-affairs/abd-vro/wiki/Secrets-Vault#setting-kubernetes-access-tokens"
  exit 2
}

# Originates from the Vault web GUI
[ "$VAULT_TOKEN" ] || {
  echo "ERROR: Missing VAULT_TOKEN"
  echo "Try running locally: scripts/set-secret-vault-token.sh <newTokenFromVaultWebGUI>"
  echo "See https://github.com/department-of-veterans-affairs/abd-vro/wiki/Secrets-Vault#setting-the-vault-token-secret"
  exit 3
}

# All secrets are in the Vault in mapi
export VAULT_ADDR=https://ldx-mapi.lighthouse.va.gov
vault login "$VAULT_TOKEN" &> /dev/null || {
  echo "ERROR: Could not log into Vault $VAULT_ADDR using VAULT_TOKEN, which may be expired."
  echo "Try running locally: scripts/set-secret-vault-token.sh <newTokenFromVaultWebGUI>"
  echo "See https://github.com/department-of-veterans-affairs/abd-vro/wiki/Secrets-Vault#setting-the-vault-token-secret"
  exit 4
}

# GitHub Team name, which used as the root path for Vault secrets
# https://github.com/orgs/department-of-veterans-affairs/teams/vro-admins/members
TEAM_NAME=vro-admins
queryVault(){
  FOLDER=$1
  >&2 echo "Querying vault at $TEAM_NAME/deploy/(default and $TARGET_ENV)/$FOLDER"
  JSON_DEFAULT=$(vault read -format=json "$TEAM_NAME/deploy/default/$FOLDER")
  if JSON_TARGET=$(vault read -format=json "$TEAM_NAME/deploy/$TARGET_ENV/$FOLDER"); then
    # Merge the JSON results, where the latter overrides the former -- https://stackoverflow.com/a/24904276
    echo "$JSON_DEFAULT" "$JSON_TARGET" | jq -s '.[0].data * .[1].data'
  else
    # Not overriding default secrets for $TARGET_ENV
    >&2 echo "(Using only secrets in 'default' folder)"
    echo "$JSON_DEFAULT" | jq '.data'
  fi
}

dumpYaml(){
  # Do not modify indentation
  echo "
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: $1
immutable: $2
data:
$3
"
}

splitSecretData(){
  echo "$1" | jq -r 'to_entries | .[] | "\(.key) \(.value)"' | while read K V; do
    >&2 echo "  - key: $K"
    # encode the value for the yaml file
    echo "  $K: $(echo -n "$V" | base64 -w0)"
  done
}
toExportCmds(){
  echo "$1" | jq -r -c 'to_entries | .[] | "\(.key) \(.value)"' | while read K V; do
    # If variable name ends with '_BASE64', decode it before exporting
    NEW_VARNAME=${K//_BASE64/}
    if [ "$NEW_VARNAME" = "$K" ]; then
      # Variable name does not contain '_BASE64', so no decoding needed
      >&2 echo "  - key: $K"
      echo "export $K='$V'"
    elif echo "$K" | grep '_BASE64$' > /dev/null; then
      DECODED="$(echo -n "$V" | base64 -d)"
      >&2 echo "  - key: $NEW_VARNAME decoded from $K"
      echo "export $NEW_VARNAME='$DECODED'"
    else
      >&2 echo "  - key: $K Unexpected; did you mean to use '_BASE64' suffix? Leaving the value as is"
      echo "export $K='$V'"
    fi
  done
}
setK8Secret(){
  # K8s secret names must only contain lowercase, '-', and '.'
  K8S_SECRET_NAME=$(echo "$1" | tr '[:upper:]_' '[:lower:]-')
  IMMUTABLE="$2"
  SECRET_DATA="$3"
  if [ "$IMMUTABLE" == "true" ]; then
    >&2 echo -e "\n## Deleting immutable secret '$K8S_SECRET_NAME'"
    kubectl -n "va-abd-rrd-${TARGET_ENV}" delete secret "$K8S_SECRET_NAME"
  fi
  >&2 echo -e "\n## Setting secret '$K8S_SECRET_NAME' from: '$VAULT_SECRET_SUFFIX'"
  dumpYaml "$K8S_SECRET_NAME" "$IMMUTABLE" "$SECRET_DATA" | \
    kubectl -n "va-abd-rrd-${TARGET_ENV}" apply --force -f - \
    || echo "!!! ERROR: Could not set secret '$K8S_SECRET_NAME'"
}

# Corresponds to subfolder of the paths to Vault secrets
SERVICE_NAMES="db mq"
# For each SERVICE_NAMES, set a vro-$VAULT_SECRET_SUFFIX K8s secret, where
# each key-value pair has a single value (a normal secret).
# These secrets are used for third-party containers that expect environment variables to be set.
for VAULT_SECRET_SUFFIX in $SERVICE_NAMES; do
  JSON=$(queryVault "$VAULT_SECRET_SUFFIX")
  SECRET_DATA=$(splitSecretData "$JSON")
  setK8Secret "vro-$VAULT_SECRET_SUFFIX" true "$SECRET_DATA"
done

# These are the env variable names, as well as part of the Vault path
VRO_SECRETS_SUFFIXES="API SLACK BIP LH BIE_KAFKA"
# Set individual `vro-secrets-*` secrets, where for each key-value pair in each secret,
#   the key begins with `VRO_SECRETS_` and the value is a multiline string consisting
#   of a series of `export VAR1=VAL1` lines. These multiline strings will be interpreted
#   and evaluated by set-env-secrets.src in each VRO container.
# Advantage: New environment variables can be added to these secrets without modifying
# Helm configurations -- simply add them to Vault, then deploy the secrets to each LHDI env.
# Do not aggregate these individual secrets into a single secret,
#   as that introduces issues with propagation of secret updates.
for VAULT_SECRET_SUFFIX in $VRO_SECRETS_SUFFIXES; do
  JSON=$(queryVault "VRO_SECRETS_$VAULT_SECRET_SUFFIX")
  # Encode exportCommands b/c it's usually multiline, plus it must be encoded for the secrets*.yaml file
  EXPORT_CMDS_BASE64=$(toExportCmds "$JSON" | base64 -w0 )
  SECRET_DATA="  VRO_SECRETS_$VAULT_SECRET_SUFFIX: $EXPORT_CMDS_BASE64"

  # Make vro-secrets-* immutable=false so that changes propagate better
  # See https://dsva.slack.com/archives/C04QLHM9LR0/p1689634792401989?thread_ts=1689604659.611619&cid=C04QLHM9LR0
  setK8Secret "vro-secrets-${VAULT_SECRET_SUFFIX}" false "$SECRET_DATA"
done

# TODO: Once all relevant pods are up or after some time, delete the secrets.
# Or use preStop hook to delete those secrets on this pod's shutdown.
# But restarted pods will fail b/c secrets aren't available.
# Or delete at least the VAULT_TOKEN secret.

# For debugging
# sleep 600
