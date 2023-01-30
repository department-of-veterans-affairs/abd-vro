#!/bin/bash

# Builds full set of self-signed certificates to simulate the Mutual TLS environment where VRO
# uses the BIP Claim Evidence API.

targetPath=$1

mkdir -p "$targetPath"
rm -f "${targetPath}"/*

# Next lines create all the private keys.
#
# The keys are not encrypted. You can get encrypted keys by specifying a cipher argument such as
# -des3 and a password argument using -passout. If keys are encrypted, some of the rest of the
# commands in this script need to be modified to specify the password.
openssl genrsa -out "${targetPath}/server_root_ca.key" 2048
openssl genrsa -out "${targetPath}/server_intermediate_ca.key" 2048
openssl genrsa -out "${targetPath}/server.key" 2048
openssl genrsa -out "${targetPath}/client_root_ca.key" 2048
openssl genrsa -out "${targetPath}/client.key" 2048

# Next lines create the self-signed public keys for server and client CA's.
openssl req -x509 -new -nodes -key "${targetPath}/server_root_ca.key" -sha256 -days 365 \
  -out "${targetPath}/server_root_ca.pem" \
  -subj "/C=US/ST=MD/L=Olney/O=./OU=./CN=localhost"
openssl req -x509 -new -nodes -key "${targetPath}/client_root_ca.key" -sha256 -days 365 \
  -out "${targetPath}/client_root_ca.pem" \
  -subj "/C=US/ST=MD/L=Rockville/O=./OU=./CN=localhost"

# Next lines create the certificate signing request (CSR) files for server intermediate CA,
# Server, and Client.
openssl req -new -sha256 -key "${targetPath}/server_intermediate_ca.key" \
  -out "${targetPath}/server_intermediate_ca.csr" \
  -subj "/C=US/ST=MD/L=Kensington/O=./OU=./CN=localhost"
openssl req -new -sha256 -key "${targetPath}/server.key" \
  -out "${targetPath}/server.csr" \
  -subj "/C=US/ST=MD/L=Bethesda/O=./OU=./CN=localhost"
openssl req -new -sha256 -key "${targetPath}/client.key" \
  -out "${targetPath}/client.csr" \
  -subj "/C=US/ST=MD/L=Baltimore/O=./OU=./CN=localhost"

# Next lines create the public key for Server Intermediate CA, Server, and Client which are
# respectively signed by Server CA, Server Intermediate CA, and Client CA.
openssl x509 -req -in "${targetPath}/server_intermediate_ca.csr"  -days 365 -sha256 \
  -CA "${targetPath}/server_root_ca.pem" \
  -CAkey "${targetPath}/server_root_ca.key" \
  -CAcreateserial \
  -out "${targetPath}/server_intermediate_ca.pem"
openssl x509 -req -in "${targetPath}/server.csr"  -days 365 -sha256 \
  -CA "${targetPath}/server_intermediate_ca.pem" \
  -CAkey "${targetPath}/server_intermediate_ca.key" \
  -CAcreateserial \
  -out "${targetPath}/server.pem"
openssl x509 -req -in "${targetPath}/client.csr"  -days 365 -sha256 \
  -CA "${targetPath}/client_root_ca.pem" \
  -CAkey "${targetPath}/client_root_ca.key" \
  -CAcreateserial \
  -out "${targetPath}/client.pem"

# Next lines create PKCS#12 files for Server and Client certificates. These files are necessary
# since Java appear not to be able to read key and pem files directly. They are read by Java
# for TLS.
openssl pkcs12 -export -password pass:server_keystore_pw -name server -nodes \
  -in "${targetPath}/server.pem" \
  -out "${targetPath}/server_keystore.p12" \
  -inkey "${targetPath}/server.key"
openssl pkcs12 -export -password pass:keystore_pw -name client -nodes \
  -in "${targetPath}/client.pem" \
  -out "${targetPath}/client_keystore.p12" \
  -inkey "${targetPath}/client.key"

# Next line concatenates Server Intermediate CA and and Server Root CA public keys into a single
# file. This is necessary for the next step while creating the Server PKCS#12 file. This is not
# necessary on the client side since there are no Intermediate Client CA's.
cat "${targetPath}/server_intermediate_ca.pem" "${targetPath}/server_root_ca.pem" > \
  "${targetPath}/server_all_cas.pem"

# Next lines creates the PKCS#12 files for Client and Server Root CA public keys. These files
# are necessary since Java appears not to be able to read the pem files directly. Java reads
# PKCS#12 files to populate Java trust stores for TLS. Same password used here to match
# previous implementation in VRO.
keytool -alias server_all_cas -noprompt -storepass keystore_pw \
  -import -file "${targetPath}/server_all_cas.pem" \
  -keystore "${targetPath}/server_truststore.p12"
keytool -alias client_root_ca -noprompt -storepass client_truststore_pw \
  -import -file "${targetPath}/client_root_ca.pem" \
  -keystore "${targetPath}/client_truststore.p12"

# When deployed VRO reads sensitive settings from Kubernetes secrets as environment variables.
# To be able to specify PKCS#12 file contents as environment variables contents are converted
# to base64 strings.
openssl base64 -in "${targetPath}/server_truststore.p12" -out "${targetPath}/server_truststore.b64"
openssl base64 -in "${targetPath}/client_keystore.p12" -out "${targetPath}/client_keystore.b64"

# Next line copies PKCS#12 files for Server keystore and Client truststore to resource directory.
# Spring Boot directly uses these files in the mock server as specified in application.yml.
cp "${targetPath}/server_keystore.p12" mock-bip-ce-api/src/main/resources
cp "${targetPath}/client_truststore.p12" mock-bip-ce-api/src/main/resources

# Next line copies PKCS#12 file base 64 content to yml files as Client keystore and Server
# truststore. These contents are used by RestTemplate in unit tests to complete Mutulal TLS.
# The same contents need to be used from VRO application when this mock server is used.
echo "vro-mock-bip-ce-keystore: >" > mock-bip-ce-api/src/test/resources/client-keystore.yml
sed 's_^_  _' "${targetPath}/client_keystore.b64" >> mock-bip-ce-api/src/test/resources/client-keystore.yml
echo "vro-mock-bip-ce-truststore: >" > mock-bip-ce-api/src/test/resources/server-truststore.yml
sed 's_^_  _' "${targetPath}/server_truststore.b64" >> mock-bip-ce-api/src/test/resources/server-truststore.yml



