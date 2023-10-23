# Intro
This module is a Spring Boot application that serves as an adapter for the BIP service.  BIP service exposes es an API via http rest.  This service 
consumes a subset of BIP api and exposes it through Rabbit MQ.  In the process, it takes care of all the complicated 
auth stuff involved in talking to BIP service.

# API 
For each api endpoint `endpoint`, there is a queue with the name `endpointQueue`.  Each endpoint expects to get an
input of a specific type and returns a value of a specific type.  The mappings are defined by methods annotated with 
`@RabbitListener` in `gov.va.vro.bip.service.RabbitMqController`  When an error occurs during the handling of the client's 
message, the api returns the same type.  The client would be wise to check `.statusCode` property of returned object.  
If it is something other than `200`, the object represents an error.  All it's properties are meaningless except 
`.statusCode` and `.statusMessage`.

# Testing
To run the integration test that assures the RMQ interactions, please, follow these steps.  
1. Make sure RabbitMq broker is running locally. For example: `docker run -t -i --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management`
2. Enable the test `sed -e 's/@Disabled/\/\/@Disabled/' -i "" "./svc-bip-api/src/test/java/gov/va/vro/bip/service/RabbitMqIntegrationTest.java"`
3. Execute the test `gradle :svc-bip-api:test --tests ov.va.vro.bip.service.RabbitMqIntegrationTest`
4. Disable test so it doesn't breat the build `git checkout -- ./svc-bip-api/src/test/java/gov/va/vro/bip/service/RabbitMqIntegrationTest.java`

# Standing it up
`gradle dockerComposeUp` should do the trick.  To stand it up separately, `gradle :svc-bip-api:bootRun`