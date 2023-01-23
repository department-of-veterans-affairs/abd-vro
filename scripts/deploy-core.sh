#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ] && [ "${ENV}" != "prod-test" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod, prod-test)" && exit 1
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

COMMON_HELM_ARGS="--set-string environment=${ENV} \
--set-string info.version=${IMAGE_TAG} \
--set-string info.git_hash=${GIT_SHA} \
--set-string info.deploy_env=${ENV} \
--set-string info.github_token=${GITHUB_ACCESS_TOKEN} \
"

: "${TEAMNAME:=va-abd-rrd}"
: "${HELM_APP_NAME:=abd-vro-core}"
# K8s namespace
NAMESPACE="${TEAMNAME}-${ENV}"

# Post an initial message so that Slack notifications from deploy-*.sh are threaded
# and SLACK_BOT_USER_OAUTH_ACCESS_TOKEN is retrieved only once from Kubernetes
source scripts/notify-slack.src "\`$0\`: ENV=\`${ENV}\` IMAGE_TAG=\`${IMAGE_TAG}\`"

# Uninstall services that are dependent on the core
./scripts/deploy-db.sh ${ENV} 0
./scripts/deploy-mq.sh ${ENV} 0
./scripts/deploy-redis.sh ${ENV} 0

source scripts/notify-slack.src "\`$0\`: Uninstalling \`${HELM_APP_NAME}\` from \`${NAMESPACE}\`"
helm del $HELM_APP_NAME -n ${NAMESPACE}
echo "Allowing time for helm to delete $HELM_APP_NAME before creating a new one"
sleep 60 # wait for Persistent Volume Claim to be deleted

source scripts/notify-slack.src "\`$0\`: Deploying new \`${HELM_APP_NAME}\` to \`${NAMESPACE}\`"
helm upgrade --install $HELM_APP_NAME helm-service-core \
              ${COMMON_HELM_ARGS} ${VRO_IMAGE_ARGS} \
              --debug \
              -n ${NAMESPACE} #--dry-run
              #-f helm-service-core/"${ENV}".yaml
