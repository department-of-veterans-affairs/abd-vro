#!/bin/sh
# This will be called by entrypoint-wrapper.sh,
# after it sets environment variables for secrets

CURR_DIR=$(pwd)
: "${CERTS_PATH:=$CURR_DIR/certs}"
mkdir -p "$CERTS_PATH"
cd "$CERTS_PATH" || exit 10

[ "$BIP_KEYSTORE" ]   || { echo "BIP_KEYSTORE is not set!"; exit 2; }
[ "$BIP_TRUSTSTORE" ] || { echo "BIP_TRUSTSTORE is not set!"; exit 3; }
[ "$BIP_PASSWORD" ]   || { echo "BIP_PASSWORD is not set!"; exit 4; }

echo "$BIP_KEYSTORE"   | base64 -d > keystore.p12
echo "$BIP_TRUSTSTORE" | base64 -d > truststore.p12

# shellcheck disable=SC2223
: ${PWD_ARG:=-password pass:$BIP_PASSWORD}
# shellcheck disable=SC2086
openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls_bip.crt -nokeys
# shellcheck disable=SC2086
openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls.key -nocerts -nodes
# shellcheck disable=SC2086
openssl pkcs12 ${PWD_ARG} -in truststore.p12 -out va_all.crt

cd "$CURR_DIR" || exit 11

exec bundle exec ruby main_consumer.rb
