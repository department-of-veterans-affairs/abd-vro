# Mock BIP Claim Evidence (CE) API

This mock API is used to test VRO implementation of integration to BIP Claim Evidence Server.
In particular the following functionality is of interest

- Mutual TLS (mTLS)
- Jwt verification (under development)
- File upload to E-Folder (under development)

## Code Generation

Certain piece of the code is automatically created from Open API Specification using. In particular
```bash
npm install @openapitools/openapi-generator-cli -g
npx @openapitools/openapi-generator-cli -i claimevidence_1.1.1_openapi.json -g spring -o code
```

## Mock BIP CE Server

The mock server is a HTTPS enabled Spring Boot Java application.

### Local Usage

The server can be run from the parent directory
```bash
./gradlew :mock-bip-ce-api:bootRun
```

### Server Certificates

The mock server uses self-signed SSL certificate for the Root Certifying Authority (CA). An
additional Intermediate CA is also used based on experimentation on GFE.

The private key `root_ca.key` for Root CA is generated using `openssl`
```bash
openssl genrsa -des3 -out root_ca.key 2048
```
with password `root_ca_pw` specified during the process. The private keys for the
Intermediate CA `intermediate_ca.key` and the Server itself `server.key` are similarly created.

The public key for the self-signed Root CA `root_ca.pem` is also created using `openssl`
```bash
openssl req -x509 -new -nodes -key root_ca.key -sha256 -days 365 -out root_ca.pem
```

The CSR for the Intermediate CA `intermediate_ca.csr` is created using `openssl`
```bash
openssl req -new -sha256 -key intermediate_ca.key -out intermediate_ca.csr
```
and signed by Root CA using
```bash
openssl x509 -req -in intermediate_ca.csr -CA root_ca.pem -CAkey root_ca.key -CAcreateserial -out intermediate_ca.pem -days 365 -sha256
```
to generate the public key `intermediate_ca.pem`.

The Server public key `server.pem` is similarly created and signed by the Intermediate CA.

### Client Certificates

To differentiate and identify the exact CA used during mTLS this project creates self-signed
certificates `client_root_ca.key` and `client_root_ca.pem` for a second Client Root CA.

### Spring Boot Configuration

Server private and public are used by the Java code using p12 files. The server p12 file
`server_keystore.p12` is generated using `openssl pkcs12`
```bash
openssl pkcs12 -export -in server.pem -out server_keystore.p12 -name server -nodes -inkey server.key
```
export password is `server_keystore_pw`.

To be able to verify client certificates, the Client CA public key `client_root_ca.pem`
is also needed for Java trust stores and generated using `keytool`
```bash
keytool -import -file client_root_ca.pem -alias client_root_ca -keystore client_truststore.p12
```
password is client_truststore.p12

The p12 files are included as Java resources and specified for the Spring Boot server in
application.yml together with passwords.

## Unit Tests

Unit tests in this project
- Verifies the functionality of the Mock Server
- Verifies how the client side certificates are specified in the VRO application

The bean coding and certificate specification can be directly used in the same manner
in the VRO application and can be a basis of end-to-end testing with the Mock server replacing the
actual BIP CE server.

### Client Certificates

The Client private key `client.key` is generated similarly to server side. The Client public key
`client.pem` is also generated similarly to server side and signed but the Client CA.

### Rest Template Configuration

The Rest Template used in the unit tests use a ssl context which use Client certificates as keys for
https and Root CA and Intermediate CA certificates as trust.

In both cases the Java stores use p12 files. The keystore p12 file `client_keystore.p12` is generated
similarly to `server_keystore.p12`.

The trust store p12 file `truststore_all.p12` is generated using the keytool
```bash
keytool -import -file all_cas.pem -alias all_cas -keystore truststore_all.p12
```
`all_cas.pem` here is the concatenation of public keys `root_ca.pem` of the Root CA
and `intermediate_ca.pem` of the Intermediate CA.

The easiest way to specify these files in kubernetes cluster is as environment variables. Thus
these files are converted to base 64 files using
```bash
openssl base64 -in <infile> -out <outfile>
```
The content of base 64 files are specified in `application-test.yml` directly for unit tests
here. In the VRO same content can be specified in environment variables
