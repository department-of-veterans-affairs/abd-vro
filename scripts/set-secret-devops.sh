#!/bin/bash

# Adds a secret needed to send Slack notifications.
# This secret is used by notify-slack.src

if [ -z "$1" ]; then
  echo "Usage: $0 \"<paste Slackbot access token>\""
  exit 1
fi

export SLACK_TOKEN_BASE64=$(echo -n "$1" | base64)
# Only need in the 1 namespace, let's use dev
scripts/echo-secret-yaml.sh "devops" "  SLACK_BOT_ACCESS_TOKEN_DEVOPS: $SLACK_TOKEN_BASE64" | \
  kubectl -n "va-abd-rrd-dev" apply -f -
