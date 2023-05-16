#!/bin/bash

# This script builds full set of self-signed certificates to simulate the Mutual TLS environment
# where VRO uses the BIP Claim Evidence API and BIP Claims API for testing purposes. In addition
# two sets of artifacts are generated and used in Java subprojects.
#
# Server artifacts are PKCS#12 files client_truststore.p12 and server_keystore.p12 which are
# directly saved as resources in subprojects mock-bip-ce-api and mock-bip-claims-api.
# Password is keystore_pw. These subprojects use the PKCS#12 to create Spring Boot https
# servers to mock BIP Claims and BIP Evidence API's.
#
# Client artifacts are yml files client-keystore.yml and server-truststore.yml which are
# included in application-local.yml in app subproject and application-test.yml's in
# mock-bip-ce-api and mock-bip-claims-api subprojects. These files introduce "keystore" and
# "truststore" properties to application context and are used to configure RestTemplate
# to make https calls.
#
# WARNING: You need to run this from a linux distro. From OSX LibreSSL it appears that
# "-addext" option is not properly working. You can use for example
# "eclipse-temurin:17-jre-alpine" which is already pulled and just run "apk add openssl"
# to add openssl and run this script.
#

targetPath="certificates" # Use a subdirectory in the current directory.

mkdir -p "$targetPath"
rm -f "${targetPath}"/*

# Next lines create all four private keys. These are for Server Root CA, Server,
# Client Root CA and, Client.
#
# The keys are not encrypted. You can get encrypted keys by specifying a cipher argument such as
# -des3 and a password argument using -passout. If keys are encrypted, some of the rest of the
# commands in this script need to be modified to specify the password.
openssl genrsa -out "${targetPath}/server_root_ca.key" 2048
openssl genrsa -out "${targetPath}/server.key" 2048
openssl genrsa -out "${targetPath}/client_root_ca.key" 2048
openssl genrsa -out "${targetPath}/client.key" 2048

# Next lines create the self-signed public keys for Server Root CA and Client Root CA. Note
# that Server Root CA public key will work for localhost, mock-bip-ce-api (docker-compose), and
# mock-bip-claims-api (docker-compose). Similarly Client Root CA public key will work for
# localhost or app (docker-compose).
openssl req -x509 -new -nodes -key "${targetPath}/server_root_ca.key" -sha256 -days 365 \
  -out "${targetPath}/server_root_ca.pem" \
  -subj "/C=US/ST=MD/L=Olney/O=./OU=./CN=RootCA" \
  -addext "subjectAltName = DNS:localhost, DNS:mock-bip-ce-api, DNS:mock-bip-claims-api"
openssl req -x509 -new -nodes -key "${targetPath}/client_root_ca.key" -sha256 -days 365 \
  -out "${targetPath}/client_root_ca.pem" \
  -subj "/C=US/ST=MD/L=Rockville/O=./OU=./CN=ClientCA" \
  -addext "subjectAltName = DNS:localhost, DNS:app"

# Next lines create the Certificate Signing Request (CSR) files for Server, and Client.
# The CSR file for Server will work for localhost, mock-bip-ce-api (docker-compose),
# and mock-bip-claims-api (docker-compose), and the CSR file for Client will work for
# localhost and app (docker-compose).
openssl req -new -sha256 -key "${targetPath}/server.key" \
  -out "${targetPath}/server.csr" \
  -subj "/C=US/ST=MD/L=Bethesda/O=./OU=./CN=Server" \
  -addext "subjectAltName = DNS:localhost, DNS:mock-bip-ce-api, DNS:mock-bip-claims-api"
openssl req -new -sha256 -key "${targetPath}/client.key" \
  -out "${targetPath}/client.csr" \
  -subj "/C=US/ST=MD/L=Baltimore/O=./OU=./CN=Client" \
  -addext "subjectAltName = DNS:localhost, DNS:app"

# Next lines create the public keys for Server and Client which are respectively signed
# by Server CA and Client CA.
openssl x509 -req -in "${targetPath}/server.csr"  -days 365 -sha256 \
  -CA "${targetPath}/server_root_ca.pem" \
  -CAkey "${targetPath}/server_root_ca.key" \
  -CAcreateserial \
  -copy_extensions copyall \
  -out "${targetPath}/server.pem"
openssl x509 -req -in "${targetPath}/client.csr"  -days 365 -sha256 \
  -CA "${targetPath}/client_root_ca.pem" \
  -CAkey "${targetPath}/client_root_ca.key" \
  -CAcreateserial \
  -copy_extensions copyall \
  -out "${targetPath}/client.pem"

# Next lines create PKCS#12 files for Server and Client certificates. These files are necessary
# since Java do read key and pem files directly for TLS.
openssl pkcs12 -export -password pass:server_keystore_pw -name server \
  -in "${targetPath}/server.pem" \
  -out "${targetPath}/server_keystore.p12" \
  -inkey "${targetPath}/server.key"
openssl pkcs12 -export -password pass:keystore_pw -name client \
  -in "${targetPath}/client.pem" \
  -out "${targetPath}/client_keystore.p12" \
  -inkey "${targetPath}/client.key"

# Next lines creates the PKCS#12 files for Client and Server Root CA public keys. These files
# are necessary since Java appears do not read the pem files directly. Java reads
# PKCS#12 files to populate Java trust stores for TLS. Same password used here to match
# previous implementation in VRO.
keytool -alias server_all_cas -noprompt -storepass keystore_pw \
  -import -file "${targetPath}/server_root_ca.pem" \
  -keystore "${targetPath}/server_truststore.p12"
keytool -alias client_root_ca -noprompt -storepass client_truststore_pw \
  -import -file "${targetPath}/client_root_ca.pem" \
  -keystore "${targetPath}/client_truststore.p12"

# When deployed VRO reads sensitive settings from Kubernetes secrets as environment variables.
# To be able to specify PKCS#12 file contents as environment variables, contents are converted
# to base64 strings.
openssl base64 -in "${targetPath}/server_truststore.p12" -out "${targetPath}/server_truststore.b64"
openssl base64 -in "${targetPath}/client_keystore.p12" -out "${targetPath}/client_keystore.b64"

# Next line copies PKCS#12 file base 64 content to yml files as Client keystore and Server
# truststore. These contents are used by RestTemplate in unit tests to complete Mutual TLS.
# The same contents are used from VRO application (app in docker-compose) when the mock servers
# are used.
echo "keystore: >" > "${targetPath}/client-keystore.yml"
sed 's_^_  _' "${targetPath}/client_keystore.b64" >> "${targetPath}/client-keystore.yml"
echo "truststore: >" > "${targetPath}/server-truststore.yml"
sed 's_^_  _' "${targetPath}/server_truststore.b64" >> "${targetPath}/server-truststore.yml"
