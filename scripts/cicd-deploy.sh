#!/bin/bash

ENV="$(tr '[A-Z]' '[a-z]' <<< $1)"

if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod)" && exit 1
fi

GIT_SHA=$(git rev-parse HEAD)
if [ -n "$2" ]
then
  IMAGE_TAG=$2
  VERSION=$2
else
  # IMAGE_TAG should match the tags on the packages:
  # - https://github.com/department-of-veterans-affairs/abd-vro/pkgs/container/abd-vro%2Fabd_vro-app
  # - https://github.com/department-of-veterans-affairs/abd-vro/pkgs/container/abd-vro%2Fabd_vro-db-init
  IMAGE_TAG=${GIT_SHA:0:7}
fi

: ${TEAMNAME:=va-abd-rrd}

# Name must match `{{ .Values.name }}-ghcr` used in api/deployment.yaml
kubectl create secret docker-registry abd-vro-ghcr -n ${TEAMNAME}-"${ENV}" \
    --docker-username="${GITHUB_USERNAME}" \
    --docker-password="${GITHUB_ACCESS_TOKEN}" \
    --docker-server='https://ghcr.io' \
    --dry-run=client -o yaml | kubectl apply -f -

# Why is this needed?
kubectl create secret generic github-access-token \
              --from-literal=GITHUB_ACCESS_TOKEN="${GITHUB_ACCESS_TOKEN}" \
              -n ${TEAMNAME}-"${ENV}" \
              --save-config --dry-run=client -o yaml | kubectl apply -f -

: ${HELM_APP_NAME:=abd-vro}
: ${VERSION:=0.0.1}
# --set-string overrides settings in helmchart/values.yaml
helm upgrade --install $HELM_APP_NAME helmchart \
              --set-string environment="${ENV}"\
              --set-string images.app.tag="${IMAGE_TAG}"\
              --set-string images.dbInit.tag="${IMAGE_TAG}"\
              --set-string info.version="${VERSION}"\
              --set-string info.git_hash="${GIT_SHA}" \
              --set-string info.deploy_env="${ENV}" \
              --set-string info.github_token="${GITHUB_ACCESS_TOKEN}" \
              --debug \
              -f helmchart/"${ENV}".yaml -n ${TEAMNAME}-"${ENV}" --wait
