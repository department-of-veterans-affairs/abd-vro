#!/bin/bash
# This script is for automated end2end testing.
# It differs from setenv.sh.

if [[ $0 == $BASH_SOURCE ]]; then
  echo "Usage: At the root of the abd-vro folder, run:
    source scripts/setenv-e2e-test.sh [path/to/abd-vro-dev-secrets]"
  exit 1
fi

export ENV=end2end-test
source scripts/setenv.sh
export SLACK_EXCEPTION_WEBHOOK=http://mock-slack:20100/slack-messages
