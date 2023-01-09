#!/bin/bash

: ${MESSAGE_TEXT:=$1}

# Set if posting in a Slack thread
: ${SLACK_THREAD_TS:=""}

# Default: C04CA47HV96 is #benefits-vro-devops
: ${SLACK_CHANNEL:=C04CA47HV96}

# Retrieve 'devops' secrets from Kubernetes and set environment vars
setEnvVarsFromK8sSecrets(){
  >&2 echo "Retrieving 'devops' secrets from Kubernetes"
  SECRETS_JSON=$(kubectl -n va-abd-rrd-dev get secret devops -o jsonpath='{.data}')
  SEC_VARS=$(echo $SECRETS_JSON | jq -r 'to_entries[] | "\(.key)=$(echo \(.value) | base64 --decode)"')
  eval $SEC_VARS
}
if [ -z "$SLACK_BOT_USER_OAUTH_ACCESS_TOKEN" ] && [ -z "$SLACK_BOT_ACCESS_TOKEN_DEVOPS" ]; then
  setEnvVarsFromK8sSecrets
  # Make it available for export it so that we don't need to query K8s for subsequent runs
  echo "export SLACK_BOT_ACCESS_TOKEN_DEVOPS=$SLACK_BOT_ACCESS_TOKEN_DEVOPS"
fi

# Use SLACK_BOT_ACCESS_TOKEN_DEVOPS by default, but a different one could be used
: ${SLACK_BOT_USER_OAUTH_ACCESS_TOKEN:=$SLACK_BOT_ACCESS_TOKEN_DEVOPS}
[ "$SLACK_BOT_USER_OAUTH_ACCESS_TOKEN" ] || { echo "SLACK_BOT_USER_OAUTH_ACCESS_TOKEN missing!"; exit 2; }

# https://api.slack.com/tutorials/tracks/posting-messages-with-curl
# https://api.slack.com/methods/chat.postMessage
# >&2 echo "Posting to Slack"
CURL_RESP=$(curl -s -d "text=$MESSAGE_TEXT" -d "channel=$SLACK_CHANNEL" -d "thread_ts=$SLACK_THREAD_TS" \
  -H "Authorization: Bearer $SLACK_BOT_USER_OAUTH_ACCESS_TOKEN" -X POST https://slack.com/api/chat.postMessage)

if [ -z "$SLACK_THREAD_TS" ]; then
  SLACK_THREAD_TS=$(echo $CURL_RESP | jq '.ts')
  echo "export SLACK_THREAD_TS=$SLACK_THREAD_TS"
fi
