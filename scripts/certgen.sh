#!/bin/bash
# -----
# Purpose: This script is used to generate BIE-Kafka-compatible, base64-encoded
#   certificate string. This script also generates the truststore and keystore passwords.
#
# output.json contains 4 key-value pairs, which should be pasted into to Vault
#
#   Following keys/values are generated
#       BIE_KAFKA_KEYSTORE_INBASE64
#       BIE_KAFKA_KEYSTORE_PASSWORD
#       BIE_KAFKA_TRUSTSTORE_INBASE64
#       BIE_KAFKA_TRUSTSTORE_PASSWORD
# Pre-requisite:
#     - Download certificates locally from the links provided at wiki https://github.com/department-of-veterans-affairs/abd-vro-internal/wiki/VRO-Secrets
#       VA-Internal-S2-ICA4.cer 
#       VA-Internal-S2-ICA19.cer
#       VA-Internal-S2-ICA11.cer 
#       VA-Internal-S2-RCA2.cer
# Input: 
# $1 Input environment name like dev, qa, prod
# $2 Input BIE provided certificate file
# $3 Input BIE provided key
# $4 Input BIE Kafka environment prefix like dev, ivv, pr, pp
# 
# - ----

# Function:  generates a random password for keystore and truststore
generate_password() {
    # Define characters for password generation
    upper="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    lower="abcdefghijklmnopqrstuvwxyz"
    numbers="0123456789"
    special="@#\$%^&*()_-+="

    # Combine all characters into one set
    all_chars="${upper}${lower}${numbers}${special}"

    # Generate a random password of length 15 using openssl
    openssl rand -base64 20 | tr -dc "$all_chars" | head -c 16
}

export KEYSTORE_PWD=$(generate_password)
export TRUSTSTORE_PWD=$(generate_password)

# Delete temporary files (if they exist) to avoid issues when re-running script.
# Keeping them will result in password mismatch error. They are safe to delete as they are recreated.
rm -f bip.truststore.jks keystore.p12 bip.truststore.p12 passwd output.json

openssl pkcs12 -export -in "$2" -inkey "$3" -out keystore.p12 -name kafka-keystore-$4-$1 -CAfile VACACerts.pem -caname root -passout env:KEYSTORE_PWD

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore bip.truststore.jks -deststoretype JKS -srcstorepass "$KEYSTORE_PWD" -deststorepass "$TRUSTSTORE_PWD"

echo "yes" | keytool -import -alias AllVA1 -file VA-Internal-S2-ICA4.cer -storetype JKS -keystore bip.truststore.jks -storepass "$TRUSTSTORE_PWD"
# shellcheck disable=SC2086
echo "yes" | keytool -import -alias AllVA2 -file VA-Internal-S2-ICA19.cer -storetype JKS -keystore bip.truststore.jks -storepass "$TRUSTSTORE_PWD"
echo "yes" | keytool -import -alias AllVA3 -file VA-Internal-S2-ICA11.cer -storetype JKS -keystore bip.truststore.jks -storepass "$TRUSTSTORE_PWD"
echo "yes" | keytool -import -alias AllVA4 -file VA-Internal-S2-RCA2.cer -storetype JKS -keystore bip.truststore.jks -storepass "$TRUSTSTORE_PWD"

echo "$KEYSTORE_PWD" | keytool -importkeystore -srckeystore bip.truststore.jks -srcstoretype jks -srcstorepass "$TRUSTSTORE_PWD" -destkeystore bip.truststore.p12 -deststoretype pkcs12 -deststorepass "$TRUSTSTORE_PWD"


# Encode the files
keystore=$(cat keystore.p12 | base64 | tr -d '\n')
bip_truststore=$(cat bip.truststore.p12 | base64 | tr -d '\n')

# Create the JSON file
echo -e "{\n\"BIE_KAFKA_KEYSTORE_INBASE64\": \"$keystore\", \n\"BIE_KAFKA_KEYSTORE_PASSWORD\": \"$KEYSTORE_PWD\", \n\"BIE_KAFKA_TRUSTSTORE_INBASE64\": \"$bip_truststore\", \n\"BIE_KAFKA_TRUSTSTORE_PASSWORD\": \"$TRUSTSTORE_PWD\"\n}" > output.json
