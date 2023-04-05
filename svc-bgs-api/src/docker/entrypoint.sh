# This will be called by entrypoint-wrapper.sh,
# after it sets environment variables for secrets

: ${CERTS_PATH:=$(pwd)/certs}
mkdir -p "$CERTS_PATH"
pushd "$CERTS_PATH"

[ "$BIP_KEYSTORE" ]   || { echo "BIP_KEYSTORE is not set!"; exit 2; }
[ "$BIP_TRUSTSTORE" ] || { echo "BIP_TRUSTSTORE is not set!"; exit 3; }
[ "$BIP_PASSWORD" ]   || { echo "BIP_PASSWORD is not set!"; exit 4; }

echo "$BIP_KEYSTORE"   | base64 -d > keystore.p12
echo "$BIP_TRUSTSTORE" | base64 -d > truststore.p12

: ${PWD_ARG:=-password pass:$BIP_PASSWORD}
openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls_bip.crt -nokeys
openssl pkcs12 ${PWD_ARG} -in keystore.p12   -out tls.key -nocerts -nodes
openssl pkcs12 ${PWD_ARG} -in truststore.p12 -out va_all.crt

popd

exec bundle exec ruby main_consumer.rb
