#!/bin/bash

# Set default environment
env=${1:-"dev"}

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "Error: kubectl is not installed."
    exit 1
fi

echo "Using environment: $env"
secret_name="va-abd-rrd-${env}-va-gov-tls"

# Clean up existing files
rm -f ca.crt tls.crt tls.key all_va_internal_cas.pem truststore.p12 keystore.p12 output.json VA-Internal-S2-ICA11.cer VA-Internal-S2-RCA2.cer

# Get and process the Kubernetes secret
secret_yaml=$(kubectl get secret "$secret_name" -o yaml)
if [ $? -ne 0 ]; then
    echo "Failed to get secret from kubectl"
    exit 1
fi

# Function to extract, decode, and save data
extract_and_save() {
    echo "$secret_yaml" | grep "$1" | awk '{print $2}' | base64 --decode > "$1"
}

# Extract, decode, and save certificates and key
extract_and_save "ca.crt"
extract_and_save "tls.crt"
extract_and_save "tls.key"

# Download and process additional certificates
curl -o AllVAInternalCAs.p7b "http://aia.pki.va.gov/PKI/AIA/VA/AllVAInternalCAs.p7b"
openssl pkcs7 -print_certs -in AllVAInternalCAs.p7b -out all_va_internal_cas.pem

# Download the specific certificates
curl -o VA-Internal-S2-ICA11.cer "http://aia.pki.va.gov/PKI/AIA/VA/VA-Internal-S2-ICA11.cer"
curl -o VA-Internal-S2-RCA2.cer "http://aia.pki.va.gov/PKI/AIA/VA/VA-Internal-S2-RCA2.cer"

# Generate a random password
PASSWORD=$(openssl rand -base64 20 | tr -dc 'A-Za-z0-9@#$%^&*()_-+=' | head -c 16)

# Create PKCS12 Keystore
openssl pkcs12 -export -in tls.crt -inkey tls.key -out keystore.p12 -passout pass:"$PASSWORD"

# Create PKCS12 Truststore and import CA certificates
keytool -import -trustcacerts -alias all_va_internal_cas -file all_va_internal_cas.pem -keystore truststore.p12 -storetype PKCS12 -storepass "$PASSWORD" -noprompt
keytool -import -trustcacerts -alias va_internal_s2_ica11 -file VA-Internal-S2-ICA11.cer -keystore truststore.p12 -storetype PKCS12 -storepass "$PASSWORD" -noprompt
keytool -import -trustcacerts -alias va_internal_s2_rca2 -file VA-Internal-S2-RCA2.cer -keystore truststore.p12 -storetype PKCS12 -storepass "$PASSWORD" -noprompt

# Base64 encode keystore and truststore
keystore=$(base64 < keystore.p12 | tr -d '\n')
truststore=$(base64 < truststore.p12 | tr -d '\n')

# Create output JSON
echo -e "{\n\"BIP_KEYSTORE_BASE64\": \"$keystore\",\n\"BIP_PASSWORD\": \"$PASSWORD\",\n\"BIP_TRUSTSTORE_BASE64\": \"$truststore\"\n}" > output.json

# Optional: Cleanup
# rm -f keystore.p12 ca.crt tls.crt tls.key truststore.p12 AllVAInternalCAs.p7b all_va_internal_cas.pem VA-Internal-S2-ICA11.cer VA-Internal-S2-RCA2.cer