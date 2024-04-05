#!/bin/bash

generate_password() {
    # Define characters for password generation
    upper="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    lower="abcdefghijklmnopqrstuvwxyz"
    numbers="0123456789"
    special="@#\$%^&*()_-+="

    # Combine all characters into one set
    all_chars="${upper}${lower}${numbers}${special}"

    # Generate a random password of length 16 using openssl
    openssl rand -base64 20 | tr -dc "$all_chars" | head -c 16
}

# Generate a password for the keystore
STOREPASS=$(generate_password)
echo "Generated keystore password: $STOREPASS"

# Define the keystore file name
KEYSTORE="vro-keystore.p12"

# Create or clear the existing keystore
rm -f "$KEYSTORE"

# Loop through the .cer files and import each into the keystore
for certfile in *.cer; do
    # Extract alias name by removing the 'VA-Internal-' prefix from the filename
    alias=$(echo "$certfile" | sed 's/VA-Internal-//; s/.cer$//')
    
    # Import the certificate into the keystore
    keytool -import -noprompt -alias "$alias" -file "$certfile" -keystore "$KEYSTORE" -storepass "$STOREPASS" -storetype PKCS12
done


# Encode the keystore file to Base64 and print it
echo "Base64 Encoded Keystore:"
base64 -i "$KEYSTORE"
