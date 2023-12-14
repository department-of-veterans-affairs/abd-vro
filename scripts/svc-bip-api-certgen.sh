#!/bin/bash

# Purpose: This script is used to generate keystore, truststore base64 encoded strings and password
# for svc-bip-api service
#
# output.json contains 3 key-value pairs, which should be pasted into to Vault
#
#   Following keys/values are generated
#       BIP_KEYSTORE_BASE64
#       BIP_PASSWORD
#       BIP_TRUSTSTORE_BASE64
#
# Pre-requisite:
#     - get kube_config file from GFE machine and set it as the default config
#     - install kubectl tool on local machine
# Input:
# $1 Input environment name like dev, qa, sandbox, prod-test, prod. Defaulted to dev if input is not provided

# Set the default environment to 'dev'
env="dev"

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "Error: kubectl is not installed. Please install kubectl and try again."
    exit 1
fi

# If a parameter is provided, use it instead of the default
if [ ! -z "$1" ]; then
    env="$1"
fi

echo "Using environment: $env"

secret_name="va-abd-rrd-${env}-va-gov-tls"

rm -f ca.crt tls.crt tls.key all_va_internal_cas.pem truststore.jks keystore.p12 truststore.p12 output.json

# Get the secret in yaml format
secret_yaml=$(kubectl get secret "$secret_name" -o yaml)

if [ $? -ne 0 ]; then
    echo "Failed to get secret from kubectl"
    exit 1
fi

# Function to extract, decode, and save the data
extract_and_save() {
    name=$1
    echo "$secret_yaml" | grep "$name" | awk '{print $2}' | base64 --decode > "$name"
    if [ $? -ne 0 ]; then
        echo "Failed to extract and save $name"
        exit 1
    fi
}

# Extract, decode, and save ca.crt, tls.crt, and tls.key
extract_and_save "ca.crt"
extract_and_save "tls.crt"
extract_and_save "tls.key"

# Function to download and save certificate files
download_cert() {
    url=$1
    filename=$(basename "$url")
    curl -o "$filename" "$url"
    if [ $? -ne 0 ]; then
        echo "Failed to download $url"
        exit 1
    fi
}

# Download additional certificate files
download_cert "http://aia.pki.va.gov/PKI/AIA/VA/AllVAInternalCAs.p7b"

openssl pkcs7 -print_certs -in AllVAInternalCAs.p7b -out all_va_internal_cas.pem

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

PASSWORD=$(generate_password)

openssl pkcs12 -export -in tls.crt -inkey tls.key -out keystore.p12 -passout env:PASSWORD

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore truststore.jks -deststoretype JKS -srcstorepass "$PASSWORD" -deststorepass "$PASSWORD"

echo yes | keytool -import -trustcacerts -alias root -file all_va_internal_cas.pem -keystore truststore.jks -storepass "$PASSWORD" -noprompt

echo "$PASSWORD" | keytool -importkeystore -srckeystore truststore.jks -srcstoretype jks -srcstorepass "$PASSWORD" -destkeystore truststore.p12 -deststoretype pkcs12 -deststorepass "$PASSWORD"


# Encode the files
keystore=$(cat keystore.p12 | base64 | tr -d '\n')
bip_truststore=$(cat truststore.p12 | base64 | tr -d '\n')

# Create the JSON file
echo -e "{\n\"BIP_KEYSTORE_BASE64\": \"$keystore\", \n\"BIP_PASSWORD\": \"$PASSWORD\", \n\"BIP_TRUSTSTORE_BASE64\": \"$bip_truststore\"\n}" > output.json

# clean up all generated/downloaded files except output.json file
rm -f keystore.p12 ca.crt tls.crt tls.key truststore.jks truststore.p12 AllVAInternalCAs.p7b all_va_internal_cas.pem
