#!/bin/bash
# -----
# Purpose: This Script is used to Generate BIE kafa Compatible Base64 Encoded
#   Certificate String. This Script also generates two passwords truststore and keystore passwords
#
# Output: Output of this script is output.json. 
#   Output.json Contains 4 key value pairs which are then uploaded to vault
#
#   Following keys/values are generated
#       BIE_KAFKA_KEYSTORE_INBASE64
#       BIE_KAFKA_KEYSTORE_PASSWORD
#       BIE_KAFKA_TRUSTSTORE_INBASE64
#       BIE_KAFKA_TRUSTSTORE_PASSWORD
# Pre-Req:
#     - Download Certs locally. Link is provided in wiki https://github.com/department-of-veterans-affairs/abd-vro-internal/wiki/VRO-Secrets
#       VA-Internal-S2-ICA4.cer 
#       VA-Internal-S2-ICA19.cer
#       VA-Internal-S2-ICA11.cer 
#       VA-Internal-S2-RCA2.cer
# Input: 
#    $1 for Environment, Example dev/qa/prod
#    $2 for BIE  provided Cert
#    $3 for BIE  provided key
#    $4 Kafka env prefix Example, dev,ivv,pr,pp
# 
# - ----
# Function to generate a random password
generate_password() {
    # Define characters for password generation
    upper="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    lower="abcdefghijklmnopqrstuvwxyz"
    numbers="0123456789"
    special="@#\$%^&*()_-+="

    # Combine all characters into one set
    all_chars="${upper}${lower}${numbers}${special}"

    # Generate a random password of length 15 using openssl
    password=$(openssl rand -base64 20 | tr -dc "$all_chars" | head -c 16)
    echo "$password"
}

export KEYSTORE_PWD=$(generate_password)
export TRUSTSTORE_PWD=$(generate_password)
rm -rf bip.truststore.jks keystore.p12 bip.truststore.p12 passwd output.json
 
echo "Key Store pwd : $KEYSTORE_PWD"
echo "Trust Store pwd: $TRUSTSTORE_PWD"

echo "Key Store pwd : $KEYSTORE_PWD" > passwd 
echo "Trust Store pwd: $TRUSTSTORE_PWD" >> passwd 

openssl pkcs12 -export -in $2 -inkey $3 -out keystore.p12 -name kafka-keystore-$4-$1 -CAfile VACACerts.pem -caname root -passout env:KEYSTORE_PWD

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore bip.truststore.jks -deststoretype JKS -srcstorepass $KEYSTORE_PWD -deststorepass $TRUSTSTORE_PWD

echo "yes" | keytool -import -alias AllVA1 -file VA-Internal-S2-ICA4.cer -storetype JKS -keystore bip.truststore.jks -storepass $TRUSTSTORE_PWD
echo "yes" | keytool -import -alias AllVA2 -file VA-Internal-S2-ICA19.cer -storetype JKS -keystore bip.truststore.jks -storepass $TRUSTSTORE_PWD
echo "yes" | keytool -import -alias AllVA3 -file VA-Internal-S2-ICA11.cer -storetype JKS -keystore bip.truststore.jks -storepass $TRUSTSTORE_PWD
echo "yes" | keytool -import -alias AllVA4 -file VA-Internal-S2-RCA2.cer -storetype JKS -keystore bip.truststore.jks -storepass $TRUSTSTORE_PWD

echo $KEYSTORE_PWD | keytool -importkeystore -srckeystore bip.truststore.jks -srcstoretype jks -srcstorepass $TRUSTSTORE_PWD -destkeystore bip.truststore.p12 -deststoretype pkcs12 -deststorepass $TRUSTSTORE_PWD


# Encode the files
keystore=$(cat keystore.p12 | base64 | tr -d '\n')
bip_truststore=$(cat bip.truststore.p12 | base64 | tr -d '\n')

# Create the JSON file
echo -e "{\n\"BIE_KAFKA_KEYSTORE_INBASE64\": \"$keystore\", \n\"BIE_KAFKA_KEYSTORE_PASSWORD\": \"$KEYSTORE_PWD\", \n\"BIE_KAFKA_TRUSTSTORE_INBASE64\": \"$bip_truststore\", \n\"BIE_KAFKA_TRUSTSTORE_PASSWORD\": \"$TRUSTSTORE_PWD\"\n}" > output.json

rm -rf bip.truststore.jks