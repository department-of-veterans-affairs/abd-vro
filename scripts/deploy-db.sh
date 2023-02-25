#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")
RESTART=$2

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ] && [ "${ENV}" != "prod-test" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod, prod-test)" && exit 1
fi

#get the current sha from github repository
GIT_SHA=$(git rev-parse HEAD)
if [ -n "$3" ]
then
  IMAGE_TAG=$3
  VERSION=$3
else
  # IMAGE_TAG should match the tags on the packages:
  # - https://github.com/department-of-veterans-affairs/abd-vro/pkgs/container/abd-vro%2Fabd_vro-app
  # - https://github.com/department-of-veterans-affairs/abd-vro/pkgs/container/abd-vro%2Fabd_vro-db-init
  IMAGE_TAG=${GIT_SHA:0:7}
  VERSION=${GIT_SHA:0:7}
fi

generateDbImageArgs(){
  local _IMAGE_TAG=$2

  # sandbox (in nonprod cluster) and prod and prod-test (in the prod cluster) requires signed-images from SecRel
  case "$1" in
    dev|qa) IMG_NAME_PREFIX="dev_";;
    sandbox|prod|prod-test) USE_SECREL_IMAGES="true";;
    *) { echo "Unknown environment: $1"; exit 20; }
  esac

  # Set USE_SECREL_IMAGES to deploy SecRel images to any ENV
  if [ "$USE_SECREL_IMAGES" ]; then
    IMG_NAME_PREFIX=""
    echo "--set-string images.repo=abd-vro-internal "
  fi

  local HELM_KEY="db"
  local IMAGE_NAME="${IMG_NAME_PREFIX}vro-postgres"
  echo "--set-string images.$HELM_KEY.tag=${_IMAGE_TAG} "
  echo "--set-string images.$HELM_KEY.imageName=${IMAGE_NAME} "
}
VRO_IMAGE_ARGS=$(generateDbImageArgs "${ENV}" "${IMAGE_TAG}")

COMMON_HELM_ARGS="--set-string environment=${ENV} \
--set-string info.version=${IMAGE_TAG} \
--set-string info.git_hash=${GIT_SHA} \
--set-string info.deploy_env=${ENV} \
--set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
"

: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro-postgres}"
# K8s namespace
NAMESPACE="${TEAMNAME}-${ENV}"

source scripts/notify-slack.src "\`$0\`: Uninstalling \`${HELM_APP_NAME}\` from \`${NAMESPACE}\`"
helm del $HELM_APP_NAME -n ${NAMESPACE}

if [ "${RESTART}" == "1" ]
then
  source scripts/notify-slack.src "\`$0\`: Deploying new \`${HELM_APP_NAME}\` to \`${NAMESPACE}\` IMAGE_TAG=\`${IMAGE_TAG}\`"

  # echo "Allowing time for helm to delete $HELM_APP_NAME before creating a new one"
  # sleep 60 # wait for Persistent Volume Claim to be deleted
  helm upgrade --install $HELM_APP_NAME helm-service-db \
              ${COMMON_HELM_ARGS} ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${NAMESPACE}
              #--dry-run
              #-f helm-service-db/"${ENV}".yaml
fi
