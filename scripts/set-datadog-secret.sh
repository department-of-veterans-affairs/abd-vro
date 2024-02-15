#!/bin/bash
# Adds details needed for datadog api.
# Script expects to be executed from the repository's root directory
# abd_vro> ./scripts/set-datadog-secret.sh TARGET_ENV DATADOG_API_KEY DATADOG_API_KEY_ID

# https://github.com/DataDog/datadog-api-client-python/blob/4f81d425324d7719fc5ee7a56c7e4bae0f3f848f/src/datadog_api_client/configuration.py#L175
# based on DataDog Python api client we need the following environment variables populated:
# * DD_SITE (see datadog python api client - https://github.com/DataDog/datadog-api-client-python/blob/4f81d425324d7719fc5ee7a56c7e4bae0f3f848f/src/datadog_api_client/configuration.py#L175)
# * DD_API_KEY (see DATADOG_API_KEY in vault)
# * DD_APP_KEY (see DATADOG_API_KEY_ID in vault)

if [[ -z "$1" || -z "$2" || -z "$3" ]]; then
  echo "Usage: $0 \"<TARGET_ENV>\" \"<paste DATADOG_API_KEY>\" \"<paste DATADOG_API_KEY_ID>\""
  exit 1
fi

: "${TARGET_ENV:=$1}"
: "${DD_SITE:=https://api.ddog-gov.com}"
: "${DD_API_KEY:=$2}"
: "${DD_APP_KEY:=$3}"

DD_SITE="$(echo -n "$DD_SITE" | base64)"
DD_API_KEY="$(echo -n "$DD_API_KEY" | base64)"
DD_APP_KEY="$(echo -n "$DD_APP_KEY" | base64)"

case "${TARGET_ENV}" in
  dev|qa|sandbox) choice="y"; echo "Executing $0 for env: $TARGET_ENV";;
  prod-test|prod) read -rp "Executing $0 for env: $TARGET_ENV Please Confirm (y/n)?" choice;;
  *)  echo "Unknown TARGET_ENV: $TARGET_ENV"
      exit 3
      ;;
esac


case "$choice" in
    y|Y ) echo "$TARGET_ENV confirmed: yes";;
    * ) echo "$TARGET_ENV was not confirmed"
        exit 4
        ;;
esac

SECRET_MAP="
  DD_SITE: \"$DD_SITE\"
  DD_API_KEY: \"$DD_API_KEY\"
  DD_APP_KEY: \"$DD_APP_KEY\"
"
./scripts/echo-secret-yaml.sh "vro-datadog" "$SECRET_MAP" | \
  kubectl -n "va-abd-rrd-$TARGET_ENV" replace --force -f -
