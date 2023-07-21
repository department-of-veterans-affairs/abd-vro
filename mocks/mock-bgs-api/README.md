# Mock BGS API

This mock API is used to test VRO implementation of integration to BGS DevelopmentNotesService API Service.
In particular the following functionality is of interest

- createNote
- createNotes
- read the WSDL for the SOAP service

## Mock BGS Server

This mock service is run on [CastleMock](https://castlemock.github.io) in a [container from DockerHub](https://hub.docker.com/r/castlemock/castlemock)
It extends the standard image, by loading the project settings file: bgs-castlemock.xml. These settings were created with the WSDL from http://bepdev.vba.va.gov/DevelopmentNotesService?wsdl
To update from a newer wsdl or add mock responses, follow the [Use Case: SOAP](https://github.com/castlemock/castlemock/wiki/Use-Case:-SOAP#import-wsdl-file) to export the settings and save this file to 
abd-vro/mocks/mock-bgs-api/src/main/resources/bgs-castlemock.xml. 

### Local Usage
The mock server can be accessed locally, [log in](https://github.com/castlemock/castlemock/wiki#how-to-use) at http://localhost:20500/castlemock

The wsdl can be queried with curl
```
curl -v "http://localhost:20500/castlemock/mock/soap/project/x5KVzK/DevelopmentNotesService?wsdl"
```
The mock createNote can be exercised with a sample request.xml
```agsl
<?xml version="1.0" encoding="UTF-8"?><env:Envelope xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tns="http://services.mapd.benefits.vba.va.gov/" xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"><env:Header><wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
  <wsse:UsernameToken>
    <wsse:Username>VROSYSACCT</wsse:Username>
  </wsse:UsernameToken>
  <vaws:VaServiceHeaders xmlns:vaws="http://vbawebservices.vba.va.gov/vawss">
    <vaws:CLIENT_MACHINE>100.103.167.166</vaws:CLIENT_MACHINE>
    <vaws:STN_ID>281</vaws:STN_ID>
    <vaws:applicationName>VRO</vaws:applicationName>
    <vaws:ExternalUid/>
    <vaws:ExternalKey/>
  </vaws:VaServiceHeaders>
</wsse:Security>
</env:Header><env:Body><tns:createNote><note><bnftClmNoteTc>CLMDVLNOTE</bnftClmNoteTc><clmId>600391838
</clmId><createDt>2023-03-22T19:39:05Z</createDt><noteOutTn>Claim Development Note</noteOutTn><ptcpntId>600033542</ptcpntId><txt>Note 3 from VRO</txt><userId>601174530</userId></note></tns:createNote></env:Body></env:Envelope>  
```

and curl:
```agsl
curl -v -H 'SOAPAction: "createNote"' -H 'Content-Type: text/xml;charset=UTF-8' --data-binary @request.xml http://localhost:20500/castlemock/mock/soap/project/x5KVzK/DevelopmentNotesService
```
