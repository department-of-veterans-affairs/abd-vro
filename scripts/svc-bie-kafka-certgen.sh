#!/bin/bash
# -----
# Purpose: This script is used to generate BIE-Kafka-compatible, base64-encoded
#   certificate string. This script also generates the truststore and keystore passwords.
#
# output.json contains 4 key-value pairs, which should be pasted into to Vault
#
#   Following keys/values are generated
#       BIE_KAFKA_TRUSTSTORE_INBASE64
#       BIE_KAFKA_TRUSTSTORE_PASSWORD
# Pre-requisite:
#     - Download all public certificates from http://aia.pki.va.gov/PKI/AIA/VA/ (ignore AllVAInternalCAs.p7b and NewVAInternalCAs.p7b)
#       locally before running this script
#

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

# Generate a password for the keystore
STOREPASS=$(generate_password)
echo "Generated keystore password: $STOREPASS"

# Define the keystore file name
TRUSTSTORE="vro-truststore.p12"

# Create or clear the existing keystore
rm -f "$TRUSTSTORE"

# Loop through the .cer files and import each into the keystore
for certfile in *.cer; do
    # Extract alias name by removing the 'VA-Internal-' prefix from the filename
    alias=$(echo "$certfile" | sed 's/VA-Internal-//; s/.cer$//')

    # Import the certificate into the keystore
    keytool -import -noprompt -alias "$alias" -file "$certfile" -keystore "$TRUSTSTORE" -storepass "$STOREPASS" -storetype PKCS12
done

# Encode the keystore file to Base64 and print it
echo "Base64 Encoded Truststore:"
base64 -i "$TRUSTSTORE"


# Encode the files
keystore=$(cat "$TRUSTSTORE" | base64 | tr -d '\n')
truststore=$(cat "$TRUSTSTORE" | base64 | tr -d '\n')

# Create the JSON file
echo -e "{\n\"BIE_KAFKA_TRUSTSTORE_INBASE64\": \"$truststore\", \n\"BIE_KAFKA_TRUSTSTORE_PASSWORD\": \"$STOREPASS\"\n}" > output.json
