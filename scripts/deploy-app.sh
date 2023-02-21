#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ] && [ "${ENV}" != "prod-test" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod, prod-test)" && exit 1
fi
KUBECLUSTER=nonprod
if [ "${ENV}" == "prod" ] || [ "${ENV}" == "prod-test" ]
then
  KUBECLUSTER=prod
fi
#get the current sha from github repository
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
  VERSION=${GIT_SHA:0:7}
fi

source scripts/image_vars.src
generateImageArgs(){
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

  for PREFIX in "${VAR_PREFIXES_ARR[@]}"; do
    local HELM_KEY=$(getVarValue "${PREFIX}" _HELM_KEY)
    local IMAGE_NAME=${IMG_NAME_PREFIX}$(getVarValue "${PREFIX}" _IMG)
    echo "--set-string images.$HELM_KEY.tag=${_IMAGE_TAG} "
    echo "--set-string images.$HELM_KEY.imageName=${IMAGE_NAME} "
  done
}
VRO_IMAGE_ARGS=$(generateImageArgs "${ENV}" "${IMAGE_TAG}")

ENDPOINT=${ENV}
if [ "${ENV}" == "prod" ]
then
  ENDPOINT=api
fi

COMMON_HELM_ARGS="--set-string environment=${ENV} \
--set-string info.version=${IMAGE_TAG} \
--set-string info.git_hash=${GIT_SHA} \
--set-string info.deploy_env=${ENV} \
--set-string endpoint=${ENDPOINT} \
--set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
--set-string images.services.environment=${KUBECLUSTER} \
\
--set-string images.redis.imageName=redis \
--set-string images.redis.tag=latest \
\
--set-string images.mq.imageName=rabbitmq \
--set-string images.mq.tag=3 \
"

: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro}"
# K8s namespace
NAMESPACE="${TEAMNAME}-${ENV}"

source scripts/notify-slack.src "\`${GITHUB_WORKFLOW:-$HOST}\`, \`$0\`: Uninstalling \`${HELM_APP_NAME}\` from \`${NAMESPACE}\` by \`${GITHUB_ACTOR:-$USER}\`"
helm del "$HELM_APP_NAME" -n "${NAMESPACE}"

source scripts/notify-slack.src "\`$0\`: Deploying new \`${HELM_APP_NAME}\` to \`${NAMESPACE}\` IMAGE_TAG=\`${IMAGE_TAG}\`"
helm upgrade --install "$HELM_APP_NAME" helm-app -n "${NAMESPACE}" \
              ${COMMON_HELM_ARGS} ${VRO_IMAGE_ARGS} \
              #--debug --dry-run \
              #-f helm-app/"${ENV}".yaml
