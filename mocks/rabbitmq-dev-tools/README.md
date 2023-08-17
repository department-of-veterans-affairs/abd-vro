
## Setup
Start RabbitMQ:
```
docker compose up -d rabbitmq-service
```

### Environment variables
Set environment variable used by the tool:
```sh
export MQ_EXCHANGE=my_exchange
export MQ_ROUTING_KEY=my_queue
export MQ_QUEUE=my_queue
```
By default, environment variable `MQ_ROUTING_KEY` is set to the value of `MQ_QUEUE`.
This is the typical configuration, but it can be customized.

## Tool modes
The tool has 4 modes, which are specified using `--args`:
- `c`: consumer listens on the specified `MQ_QUEUE` for `N` messages (default: `N=100`)
- `p`: producer pushes a message with `MSG_BODY` to the specified `MQ_EXCHANGE` with the specified `MQ_ROUTING_KEY`, which is routed to the specified `MQ_QUEUE`
- `resp`: responder
- `req`: requester

To understand the implementation of each mode, examine the `MessageConsumer`, `MessageProducer`, ...  classes.

## Consumer-Producer example

Open a new console, and set up consumer to listen to 3 messages:
```sh
export MQ_EXCHANGE=my_exchange
export MQ_QUEUE=my_queue
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="c 3"
```

Open a browser to http://localhost:15672/#/exchanges and http://localhost:15672/#/queues to examine RabbitMQ.

Open a new console, and have producer send a message that will end up in the consumer's queue:
```sh
export MQ_EXCHANGE=my_exchange
export MQ_ROUTING_KEY=my_queue
export MSG_BODY='{"someField":"12345"}'
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="p"
```

In the consumer console, expect to see the following output:
```
g.v.v.t.r.RabbitMqToolApplication        : Consumer waiting for message 1
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyString: {"someField":"12345"}
gov.va.vro.tools.rabbitmq.MessageHelper  :   MessageProperties [headers={}, contentType=application/octet-stream, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=my_exchange, receivedRoutingKey=my_queue, deliveryTag=1]
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyConverted (class java.util.LinkedHashMap): {someField=12345}
g.v.v.t.r.RabbitMqToolApplication        : Consumer waiting for message 2
```

Visit the consumer's queue http://localhost:15672/#/queues/%2F/my_queue to see a spike in the message chart.

## Responder-Requester example

Open a new console, and set up a responder to listen on queue `serviceJ` bound to exchange `xample`:
```sh
export MQ_EXCHANGE=xample
export MQ_QUEUE=serviceJ
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="resp"
```

Open a new console, and have a requester send a JSON message:
```sh
export MQ_EXCHANGE=xample
export MQ_ROUTING_KEY=serviceJ
export MSG_BODY='{"resourceId":"12343","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}'
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="req"
```

Expect this output, noting the response `header` now states `statusCode=200`:
```
g.v.v.t.r.RabbitMqToolApplication        : Requester
gov.va.vro.tools.rabbitmq.MessageHelper  :   buildMessage(String): {"resourceId":"12343","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyString: {"resourceId":"12343","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   MessageProperties [headers={}, contentType=application/octet-stream, contentLength=0, deliveryMode=PERSISTENT, priority=0, deliveryTag=0]
o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#57d0fc89:0/SimpleConnection@6f3e19b3 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 59021]
.l.DirectReplyToMessageListenerContainer : Container initialized for queues: [amq.rabbitmq.reply-to]
.l.DirectReplyToMessageListenerContainer : SimpleConsumer [queue=amq.rabbitmq.reply-to, index=0, consumerTag=amq.ctag-Y20ezDijzdHrOl5try_c8g identity=654c7d2d] started
g.v.vro.tools.rabbitmq.MessageRequester  : Got response
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyString: {"resourceId":"12343","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":200,"statusMessage":"MessageResponder's response"}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   MessageProperties [headers={__ContentTypeId__=java.lang.Object, __KeyTypeId__=java.lang.Object}, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=, receivedRoutingKey=amq.rabbitmq.reply-to.g1h2AA5yZXBseUAxMDY2MzAxMgAAHwgAAAAAZNEB+g==.a8L2ifLvFJgzxEHd9ONrDQ==, deliveryTag=1, consumerTag=amq.ctag-Y20ezDijzdHrOl5try_c8g, consumerQueue=amq.rabbitmq.reply-to]
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyConverted (class java.util.LinkedHashMap): {resourceId=12343, diagnosticCode=J, status=PROCESSING, header={statusCode=200, statusMessage=MessageResponder's response}}
.l.DirectReplyToMessageListenerContainer : Successfully waited for consumers to finish.
```

### Slow Responder
To simulate responder that takes a long time to respond, back in the responder console, set a very long `RESPONSE_DELAY_MILLIS`:
```sh
export RESPONSE_DELAY_MILLIS=100000
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="resp"
```

And back in the requester console, set a short `RESPONSE_TIMEOUT_MILLIS`:
```sh
export RESPONSE_TIMEOUT_MILLIS=2000
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="req"
```

Expect in the requester console:
```
g.v.vro.tools.rabbitmq.MessageRequester  : No response
.l.DirectReplyToMessageListenerContainer : Successfully waited for consumers to finish.
```

## Test a microservice
Open a new console, and start the Xample-J microservice:
```sh
cd domain-xample/
docker compose up svc-xample-j
```

Open a new console, and have a requester send an expected JSON message in the expected exchange:
```sh
export MQ_EXCHANGE=xample
export MQ_ROUTING_KEY=serviceJ
export RESPONSE_TIMEOUT_MILLIS=5000
export MSG_BODY='{"resourceId":"54321","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}'
./gradlew -p mocks :rabbitmq-dev-tools:bootRun --args="req"
```

Expect to see the request and response:
```
g.v.v.t.r.RabbitMqToolApplication        : Requester
gov.va.vro.tools.rabbitmq.MessageHelper  :   buildMessage(String): {"resourceId":"54321","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyString: {"resourceId":"54321","diagnosticCode":"J","status":"PROCESSING","header":{"statusCode":0,"statusMessage":null}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   MessageProperties [headers={}, contentType=application/octet-stream, contentLength=0, deliveryMode=PERSISTENT, priority=0, deliveryTag=0]
o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#2679311f:0/SimpleConnection@5b22d8a1 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 60326]
.l.DirectReplyToMessageListenerContainer : Container initialized for queues: [amq.rabbitmq.reply-to]
.l.DirectReplyToMessageListenerContainer : SimpleConsumer [queue=amq.rabbitmq.reply-to, index=0, consumerTag=amq.ctag-g2gqkNhL88ZqtzlDjW_f5w identity=6dd1c3ed] started
g.v.vro.tools.rabbitmq.MessageRequester  : Got response
gov.va.vro.tools.rabbitmq.MessageHelper  :   bodyString: {"resourceId":"54321","diagnosticCode":"J","status":"DONE","header":{"statusCode":200,"statusMessage":null}}
gov.va.vro.tools.rabbitmq.MessageHelper  :   MessageProperties [headers={__TypeId__=gov.va.vro.model.xample.SomeDtoModel}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=, receivedRoutingKey=amq.rabbitmq.reply-to.g1h2AA5yZXBseUAxMDY2MzAxMgAABloAAAABZNEB+g==.xWks3LFqEJIDFXTxSXEXxA==, deliveryTag=1, consumerTag=amq.ctag-g2gqkNhL88ZqtzlDjW_f5w, consumerQueue=amq.rabbitmq.reply-to]
gov.va.vro.tools.rabbitmq.MessageHelper  :   Could not convert body! Returning body as String
.l.DirectReplyToMessageListenerContainer : Successfully waited for consumers to finish.
```

In the Xample-J microservice console, expect:
```
g.v.v.s.xample.XampleJavaMicroservice    : Received: SomeDtoModel(resourceId=54321, diagnosticCode=J, status=PROCESSING, header=SomeDtoModelHeader(statusCode=0, statusMessage=null))
```
