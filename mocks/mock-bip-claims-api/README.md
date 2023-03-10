# Mock BIP Claims API

This mock API is used to test integration of BIP Claims API in end-to-end and local testing.

## Code Generation

This initial version of this API is automatically created using the Open API Specification of the
BIP Claims API using
```bash
npm install @openapitools/openapi-generator-cli -g
npx @openapitools/openapi-generator-cli generate -i bipclaim_3.1.1.json -g spring -o code
```
`bipclaim_3.1.1.json` can be downloaded from BIP UAT Server.
