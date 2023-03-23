#!/bin/bash

# Adds a secret needed to send Slack notifications.
# This secret is used by notify-slack.src

if [ -z "$1" ]; then
  echo "Usage: $0 \"<paste Slackbot access token>\""
  exit 1
fi

: ${SLACK_TOKEN:=$1}

[ "$SLACK_TOKEN" ] || { echo "Missing SLACK_TOKEN"; exit 3; }

dumpYaml(){
  # Do not modify indentation
  echo "
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: $1
data:
$2
"
}

export SLACK_TOKEN_BASE64=$(echo -n "$SLACK_TOKEN" | base64)
# Only need in the 1 namespace, let's use dev
dumpYaml "devops" "  SLACK_BOT_ACCESS_TOKEN_DEVOPS: $SLACK_TOKEN_BASE64" | \
  kubectl -n "va-abd-rrd-dev" apply -f -
