#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod)" && exit 1
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
: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro}"
helm del $HELM_APP_NAME -n ${TEAMNAME}-${ENV}
helm upgrade --install $HELM_APP_NAME helmchart \
              --set-string environment=${ENV} \
              --set-string images.app.tag=${IMAGE_TAG} \
              --set-string images.redis.tag=latest \
              --set-string images.db.tag=${IMAGE_TAG} \
              --set-string images.mq.tag="3" \
              --set-string images.dbInit.tag=${IMAGE_TAG} \
              --set-string images.pdfGenerator.tag=${IMAGE_TAG} \
              --set-string images.serviceAssessClaimDC7101.tag=${IMAGE_TAG} \
              --set-string images.serviceAssessClaimDC6602.tag=${IMAGE_TAG} \
              --set-string images.serviceDataAccess.tag=${IMAGE_TAG} \
              --set-string info.version=${IMAGE_TAG} \
              --set-string info.git_hash=${GIT_SHA} \
              --set-string info.deploy_env=${ENV} \
              --set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
              --set-string images.app.imageName=${ENV}_vro-app \
              --set-string images.redis.imageName=redis \
              --set-string images.db.imageName=${ENV}_vro-postgres \
              --set-string images.mq.imageName=vro-rabbitmq \
              --set-string images.dbInit.imageName=${ENV}_vro-db-init \
              --set-string images.pdfGenerator.imageName=${ENV}_vro-service-pdfgenerator \
              --set-string images.serviceAssessClaimDC7101.imageName=${ENV}_vro-service-assessclaimdc7101 \
              --set-string images.serviceAssessClaimDC6602.imageName=${ENV}_vro-service-assessclaimdc6602 \
              --set-string images.serviceDataAccess.imageName=${ENV}_vro-service-data-access \
              --debug \
              -n ${TEAMNAME}-"${ENV}" #--dry-run
              #-f helmchart/"${ENV}".yaml