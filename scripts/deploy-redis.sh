#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")
RESTART=$2

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
  IMAGE_TAG=${GIT_SHA:0:7}
  VERSION=${GIT_SHA:0:7}
fi

COMMON_HELM_ARGS="--set-string environment=${ENV} \
--set-string info.version=${IMAGE_TAG} \
--set-string info.git_hash=${GIT_SHA} \
--set-string info.deploy_env=${ENV} \
--set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
\
--set-string images.redis.imageName=redis \
--set-string images.redis.tag=latest \
"

: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro-redis}"
# K8s namespace
NAMESPACE="${TEAMNAME}-${ENV}"

helm del $HELM_APP_NAME -n ${NAMESPACE}

if [ "${RESTART}" == "1" ]
then
# If SLACK_THREAD_TS is set, the notification will be in a Slack thread
NOTIFY_SLACK_OUT=$(scripts/notify-slack.sh "\`$0\`: ENV=\`${ENV}\` IMAGE_TAG=\`${IMAGE_TAG}\` ...")
# If SLACK_THREAD_TS is not set, run eval so that SLACK_THREAD_TS env var is available for subsequent notifications in a Slack thread
[ "$SLACK_THREAD_TS" ] || eval "$NOTIFY_SLACK_OUT"

# echo "Allowing time for helm to delete $HELM_APP_NAME before creating a new one"
# sleep 60 # wait for Persistent Volume Claim to be deleted
helm upgrade --install $HELM_APP_NAME helm-service-redis \
              ${COMMON_HELM_ARGS} ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${NAMESPACE} #--dry-run
              #-f helm-service-redis/"${ENV}".yaml
fi
