# Mock BIP Claim Evidence Server

This server mocks mTLS authentication scheme, jwt verification, and upload file end points of BIP 
Claim Evidence Server. Mock is used for testing and proof of concept.

## Mutual TLS (mTLS)

We will have 3 entities for the server: root ca, intermediate ca, and server. We will also have an enioty for testing.

The private key for the root ca is created using
```bash
openssl genrsa -des3 -out root_ca.key 2048
```
with password `root_ca_pw`. The others are similarly created.

Self-signed root server abd client certificates are created using
```bash
openssl req -x509 -new -nodes -key root_ca.key -sha256 -days 365 -out root_ca.pem
```

Server csr are created using
```bash
openssl req -new -sha256 -key server.key -out server.csr
```

The intermediate ca certificate in `intermediate_ca.csr` is signed by
```bash
openssl x509 -req -in intermediate_ca.csr -CA root_ca.pem -CAkey root_ca.key -CAcreateserial -out intermediate_ca.pem -days 365 -sha256
```
Similarly for others

Pk12 file is created
```bash
openssl pkcs12 -export -in server.pem -out server_keystore.p12 -name server -nodes -inkey server.key
```
export password is server_keystore_pw.

Keytool can be used
```bash
keytool -import -file client_root_ca.pem -alias client_root_ca -keystore client_truststore.p12
```
password is client_truststore.p12

PKS12 files can be overted to base64
```bash
openssl base64 -in <infile> -out <outfile>
```

exported to environment variables MOCK_BIP_CE_TRUSTSTORE, MOCK_BIP_CE_KEYSTORE
