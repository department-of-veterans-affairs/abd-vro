#!/bin/bash
# Adds a secret needed to access both clusters.

[ -e "$HOME/.kube/config" ] || { echo "Expecting $HOME/.kube/config for the non-prod cluster"; exit 2; }
[ -e "$HOME/.kube/config-prod" ] || { echo "Expecting $HOME/.kube/config-prod for the prod cluster"; exit 3; }

SECRET_MAP="
  DEV_KUBE_CONFIG: $(base64 < "$HOME/.kube/config")
  PROD_KUBE_CONFIG: $(base64 < "$HOME/.kube/config-prod")
"
scripts/echo-secret-yaml.sh "devops-kubeconfig" "$SECRET_MAP" | \
  kubectl -n "va-abd-rrd-dev" replace --force -f -
