#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" "${ENV}" != "prod-test" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod, prod-test)" && exit 1
fi

if [ "${GITHUB_ACCESS_TOKEN}" == "" ]
then
  echo "please set your github access token environment variable (export GITHUB_ACCESS_TOKEN=XXXXXX)" && exit 2
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

  case "$1" in
    dev|qa) IMG_NAME_PREFIX="${1}_";;
    sandbox|prod|prod-test) IMG_NAME_PREFIX="";;
    *) { echo "Unknown environment: $1"; exit 20; }
  esac

  for PREFIX in ${VAR_PREFIXES_ARR[@]}; do
    local HELM_KEY=`getVarValue ${PREFIX} _HELM_KEY`
    local IMAGE_NAME=${IMG_NAME_PREFIX}`getVarValue ${PREFIX} _IMG`
    echo "--set-string images.$HELM_KEY.tag=${_IMAGE_TAG} "
    echo "--set-string images.$HELM_KEY.imageName=${IMAGE_NAME} "
  done
}
VRO_IMAGE_ARGS=$(generateImageArgs "${ENV}" "${IMAGE_TAG}")

if [ "${ENV}" == "qa" ] || [ "${ENV}" == "dev" ]
then
: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro}"
helm del $HELM_APP_NAME -n ${TEAMNAME}-${ENV}
helm upgrade --install $HELM_APP_NAME helmchart \
              --set-string environment=${ENV} \
              --set-string info.version=${IMAGE_TAG} \
              --set-string info.git_hash=${GIT_SHA} \
              --set-string info.deploy_env=${ENV} \
              --set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
              --set-string images.redis.tag=latest \
              --set-string images.redis.imageName=redis \
              --set-string images.mq.tag="3" \
              --set-string images.mq.imageName=vro-rabbitmq \
              ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${TEAMNAME}-${ENV} #--dry-run
              #-f helmchart/"${ENV}".yaml
elif [ "${ENV}" == "sandbox" ] || [ "${ENV}" == "prod" || "${ENV}" == "prod-test" ]
then
: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro}"
helm del $HELM_APP_NAME -n ${TEAMNAME}-"${ENV}"
helm upgrade --install $HELM_APP_NAME helmchart \
              --set-string images.repo=abd-vro-internal \
              --set-string environment="${ENV}"\
              --set-string info.version="${IMAGE_TAG}"\
              --set-string info.git_hash="${GIT_SHA}" \
              --set-string info.deploy_env="${ENV}" \
              --set-string info.github_token="${GITHUB_ACCESS_TOKEN}" \
              --set-string images.redis.tag="latest"\
              --set-string images.redis.imageName=redis \
              --set-string images.mq.tag="3"\
              --set-string images.mq.imageName=rabbitmq \
              ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${TEAMNAME}-"${ENV}"
else
helm del abd-vro
helm upgrade --install abd-vro helmchart \
              --set-string images.repo=abd-vro-internal \
              --set-string environment="${ENV}"\
              --set-string info.version="${IMAGE_TAG}"\
              --set-string info.git_hash="${GIT_SHA}" \
              --set-string info.deploy_env="${ENV}" \
              --set-string info.github_token="${GITHUB_ACCESS_TOKEN}" \
              --set-string images.redis.imageName=redis \
              --set-string images.redis.tag="latest"\
              --set-string images.mq.imageName=rabbitmq \
              --set-string images.mq.tag="3"\
              ${VRO_IMAGE_ARGS} \
              --debug
fi
