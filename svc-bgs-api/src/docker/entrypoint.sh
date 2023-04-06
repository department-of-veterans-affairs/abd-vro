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

    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls_bip.crt -nokeys || exit 2
    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls.key -nocerts -nodes || exit 3
    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in truststore.p12 -out va_all.crt || exit 4
}

cd "$CURR_DIR" || exit 11

exec bundle exec ruby main_consumer.rb
