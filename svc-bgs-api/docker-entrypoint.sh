#!/bin/sh
# This will be called by entrypoint-wrapper.sh,
# after it sets environment variables for secrets

CURR_DIR=$(pwd)
: "${CERTS_PATH:=$CURR_DIR/certs}"
mkdir -p "$CERTS_PATH"
cd "$CERTS_PATH" || exit 10

[ "$BIP_KEYSTORE" ]   || { echo "BIP_KEYSTORE is not set!"; }
[ "$BIP_TRUSTSTORE" ] || { echo "BIP_TRUSTSTORE is not set!"; }
[ "$BIP_PASSWORD" ]   || { echo "BIP_PASSWORD is not set!"; }

# shellcheck disable=SC2223
[ "$BIP_PASSWORD" ] && { : ${PWD_ARG:=-password pass:$BIP_PASSWORD}; }

[ "$BIP_KEYSTORE" ] && [ "$BIP_TRUSTSTORE" ] && {
    echo "$BIP_KEYSTORE"   | base64 -d > keystore.p12
    echo "$BIP_TRUSTSTORE" | base64 -d > truststore.p12
}

cd "$CURR_DIR" || exit 11

exec bundle exec ruby main_consumer.rb
