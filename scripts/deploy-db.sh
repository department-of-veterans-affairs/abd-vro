#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")
RESTART=$(tr '[0-1]' <<< "$2")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ] && [ "${ENV}" != "prod-test" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod, prod-test)" && exit 1
fi

if [ "${GITHUB_ACCESS_TOKEN}" == "" ]
then
  echo "please set your github access token environment variable (export GITHUB_ACCESS_TOKEN=XXXXXX)" && exit 2
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

source scripts/image_vars_db.src
generateImageArgs(){
  local _IMAGE_TAG=$3

  # sandbox (in nonprod cluster) and prod and prod-test (in the prod cluster) requires signed-images from SecRel
  case "$1" in
    dev|qa) IMG_NAME_PREFIX="${1}_";;
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

helm del $HELM_APP_NAME -n ${NAMESPACE}
echo "Allowing time for helm to delete $HELM_APP_NAME before creating a new one"
#sleep 60 # wait for Persistent Volume Claim to be deleted
if [ "${RESTART}" == "1" ]
then
helm upgrade --install $HELM_APP_NAME helm-service-db \
              ${COMMON_HELM_ARGS} ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${NAMESPACE} #--dry-run
              #-f helm-service-db/"${ENV}".yaml
fi