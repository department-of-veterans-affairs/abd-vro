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

# Checks that openssl is installed
openssl version

[ "$BIP_KEYSTORE" ] && [ "$BIP_TRUSTSTORE" ] && {
    echo "$BIP_KEYSTORE"   | base64 -d > keystore.p12
    echo "$BIP_TRUSTSTORE" | base64 -d > truststore.p12

    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls_bip.crt -nokeys -legacy || exit 2
    echo "Emitted tls_bip.crt"

    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls.key -nocerts -nodes -legacy || exit 3
    echo "Emitted tls.key"

    # shellcheck disable=SC2086
    openssl pkcs12 ${PWD_ARG} -in truststore.p12 -out va_all.crt -legacy || exit 4
    echo "Emitted va_all.crt"
}

cd "$CURR_DIR" || exit 11

echo "Executing Ruby application..."
exec bundle exec ruby main_consumer.rb
