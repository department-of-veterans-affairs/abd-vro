#!/bin/sh

getEnvVarValue(){
  local VARNAME="$1"
  eval echo "\$$VARNAME"
}

for ENV_VAR in \
  BIE_KAFKA_TRUSTSTORE_INBASE64 BIE_KAFKA_TRUSTSTORE_PASSWORD; do
  if [ "$(getEnvVarValue "$ENV_VAR")" = "" ]; then
    >&2 echo "ERROR: Missing expected environment variable: $ENV_VAR"
  fi
done

export KEYSTORE_FILE="$PWD/keystore.p12"
echo "$BIE_KAFKA_KEYSTORE_INBASE64" | base64 -d > "$KEYSTORE_FILE"
echo -e "\nVerifying keystore ($KEYSTORE_FILE) and its password..."
if ! keytool -list -v -keystore "$KEYSTORE_FILE" -storepass "$BIE_KAFKA_KEYSTORE_PASSWORD" | grep "Alias name:"; then
  >&2 echo "ERROR: with keystore"
fi

export TRUSTSTORE_FILE="$PWD/truststore.p12"
echo "$BIE_KAFKA_TRUSTSTORE_INBASE64" | base64 -d > "$TRUSTSTORE_FILE"
echo -e "\nVerifying truststore ($TRUSTSTORE_FILE) and its password..."
if ! keytool -list -v -keystore "$TRUSTSTORE_FILE" -storepass "$BIE_KAFKA_TRUSTSTORE_PASSWORD" | grep "Alias name:"; then
  >&2 echo "ERROR: with truststore"
fi
