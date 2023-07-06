#!/bin/bash
# This script runs in the gh-runner container to set K8s secrets needed by VRO containers.

NAMESPACE=$(cat /run/secrets/kubernetes.io/serviceaccount/namespace)
: "${TARGET_ENV:=${NAMESPACE#va-abd-rrd-}}"
echo "TARGET_ENV=$TARGET_ENV"

if [ "$KUBE_CONFIG" ]; then
  # Enable setting secrets in either cluster
  mkdir -p ~/.kube
  echo -n "${KUBE_CONFIG}" > ~/.kube/config
  chmod go-rwx ~/.kube/config
else
  echo "Missing KUBE_CONFIG. Using kubectl namespace of container."
fi

# Originates from the Vault web GUI
[ "$VAULT_TOKEN" ] || { echo "Missing VAULT_TOKEN"; exit 3; }

# All secrets are in the Vault in mapi
export VAULT_ADDR=https://ldx-mapi.lighthouse.va.gov
vault login "$VAULT_TOKEN" &> /dev/null || { echo "Could not log into Vault $VAULT_ADDR using VAULT_TOKEN"; exit 4; }

# GitHub Team name, which used as the root path for Vault secrets
# https://github.com/orgs/department-of-veterans-affairs/teams/vro-admins/members
TEAM_NAME=vro-admins
queryVault(){
  FOLDER=$1
  >&2 echo "Querying vault at $TEAM_NAME/deploy/(default|$TARGET_ENV)/$FOLDER"
  JSON_DEFAULT=$(vault read -format=json "$TEAM_NAME/deploy/default/$FOLDER")
  if JSON_TARGET=$(vault read -format=json "$TEAM_NAME/deploy/$TARGET_ENV/$FOLDER"); then
    # Merge the JSON results, where the latter overrides the former -- https://stackoverflow.com/a/24904276
    echo "$JSON_DEFAULT" "$JSON_TARGET" | jq -s '.[0].data * .[1].data'
  else
    # Not overriding default secrets for $TARGET_ENV
    >&2 echo "(Using only default secrets)"
    echo "$JSON_DEFAULT" | jq '.data'
  fi
}

dumpYaml(){
  # Do not modify indentation
  echo "
apiVersion: v1
kind: Secret
type: Opaque
immutable: true
metadata:
  name: $1
data:
$2
"
}

splitSecretData(){
  echo "$1" | jq -r 'to_entries | .[] | "\(.key) \(.value)"' | while read -r K V; do
    >&2 echo "  - key: $K"
    # encode the value for the yaml file
    echo "  $K: $(echo -n "$V" | base64 -w0)"
  done
}
toExportCmds(){
  echo "$1" | jq -r -c 'to_entries | .[] | "\(.key) \(.value)"' | while read -r K V; do
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
collectSecretExportCmds(){
  for VRO_SECRETS in "$@"; do
    >&2 echo -e "\n## Setting secret 'vro-secrets': '$VRO_SECRETS'"
    JSON=$(queryVault "$VRO_SECRETS")
    # Encode exportCommands b/c it's usually multiline, plus it must be encoded for the secrets*.yaml file
    EXPORT_CMDS_BASE64=$(toExportCmds "$JSON" | base64 -w0 )
    echo "  $VRO_SECRETS: $EXPORT_CMDS_BASE64"
  done
}

# Corresponds to subfolder of the paths to Vault secrets
SERVICE_NAMES="db mq redis"
# For each SERVICE_NAMES, set a SERVICE_NAME secret, where
# each key-value pair has a single value (a normal secret).
# These secrets are used for third-party containers that expect environment variables to be set.
for SERVICE_NAME in $SERVICE_NAMES; do
  >&2 echo -e "\n## Setting secret 'vro-$SERVICE_NAME'"
  JSON=$(queryVault "$SERVICE_NAME")
  SERVICE_SECRET_DATA=$(splitSecretData "$JSON")
  dumpYaml "vro-$SERVICE_NAME" "$SERVICE_SECRET_DATA" | \
    kubectl -n "va-abd-rrd-${TARGET_ENV}" replace --force -f -
done

# These are the env variable names, as well as part of the Vault path
VRO_SECRETS_NAMES="VRO_SECRETS_API VRO_SECRETS_SLACK VRO_SECRETS_MAS VRO_SECRETS_BIP VRO_SECRETS_LH"
# Set the `vro-secrets` secret, where for each key-value pair,
# the key begins with `VRO_SECRETS_` and the value is a multiline string consisting
# of a series of `export VAR1=VAL1` lines. These multiline strings will be interpreted
# and evaluated by set-env-secrets.src in each VRO container.
# Advantage: New environment variables can be added to these secrets without modifying
# Helm configurations -- simply add them to Vault.
SECRET_DATA=$(collectSecretExportCmds "$VRO_SECRETS_NAMES")
dumpYaml vro-secrets "$SECRET_DATA" | \
  kubectl -n "va-abd-rrd-${TARGET_ENV}" replace --force -f -

# TODO: Once all relevant pods are up or after some time, delete the secrets.
# Or use preStop hook to delete those secrets on this pod's shutdown.
# But restarted pods will fail b/c secrets aren't available.
# Or delete at least the VAULT_TOKEN secret.

# For debugging
sleep 600
