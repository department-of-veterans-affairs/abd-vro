#!/bin/bash

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "Error: kubectl is not installed. Please install kubectl and try again."
    exit 1
fi

rm -f ca.crt tls.crt tls.key VA-Internal-S2-RCA1-v1.cer VA-Internal-S2-ICA4.cer truststore.jks keystore.p12 truststore.p12 output.json

# Get the secret in yaml format
secret_yaml=$(kubectl get secret va-abd-rrd-dev-va-gov-tls -o yaml)

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
download_cert "http://aia.pki.va.gov/PKI/AIA/VA/VA-Internal-S2-RCA1-v1.cer"
download_cert "http://aia.pki.va.gov/PKI/AIA/VA/VA-Internal-S2-ICA4.cer"

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


echo "yes" | keytool -import -alias rca1 -file VA-Internal-S2-RCA1-v1.cer -storetype JKS -keystore truststore.jks -storepass "$PASSWORD"
echo "yes" | keytool -import -alias ica4 -file VA-Internal-S2-ICA4.cer -storetype JKS -keystore truststore.jks -storepass "$PASSWORD"

echo "$PASSWORD" | keytool -importkeystore -srckeystore truststore.jks -srcstoretype jks -srcstorepass "$PASSWORD" -destkeystore truststore.p12 -deststoretype pkcs12 -deststorepass "$PASSWORD"


# Encode the files
keystore=$(cat keystore.p12 | base64 | tr -d '\n')
bip_truststore=$(cat truststore.p12 | base64 | tr -d '\n')

# Create the JSON file
echo -e "{\n\"BIP_KEYSTORE_BASE64\": \"$keystore\", \n\"BIP_PASSWORD\": \"$PASSWORD\", \n\"BIP_TRUSTSTORE_BASE64\": \"$bip_truststore\"\n}" > output.json
