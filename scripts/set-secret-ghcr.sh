#!/bin/bash

# Adds a secret needed to pull images from GHCR.

if [ -z "$2" ]; then
  echo "Usage: $0 TARGET_ENV \"<GitHub personal access token>\""
  exit 1
fi

: ${TARGET_ENV:=$1}
: ${GH_PAT:=$2}

case "${TARGET_ENV}" in
  dev|qa|sandbox) KUBE_ARGS="";;
  prod-test|prod) KUBE_ARGS="--kubeconfig $HOME/.kube/config-prod";;
  *)  echo "Unknown TARGET_ENV: $TARGET_ENV"
      exit 3
      ;;
esac

SECRET_NAME="devops-ghcr"
kubectl -n "va-abd-rrd-$TARGET_ENV" $KUBE_ARGS create secret docker-registry $SECRET_NAME \
  --docker-server="https://ghcr.io" \
  --docker-username="abd-vro-machine" \
  --docker-password="${GH_PAT}"
