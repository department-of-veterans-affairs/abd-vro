To investigate and recover from errors particularly in the production environment, a VRO Console container was implemented and took inspiration from Rails Console.

The VRO Console container (or simply Console) facilitates diagnostics, such as examining the processing state of the claim by looking at Camel routes and the DB contents, and realtime updates to VRO's state in Redis and VRO's DB.

## Enable Console Locally

- `export COMPOSE_PROFILES=debug` -- the console container only starts if the docker-compose `debug` profile is enabled.
- Start VRO. Note the `vro-console-1` container; check out the logs: `docker logs vro-console-1`.
- Attach to the container: `docker attach vro-console-1`

If you need to restart the console container:
```
cd app/src/docker
docker-compose up -d console
docker attach vro-console-1
```

## Connect to deployed Console container
In order to connect to Console container deployed in LHDI, set up `kubectl` using the [[Lightkeeper tool]].

Next to connect to the DEV deployment:
```bash
# For convenience
❯ alias kc='kubectl -n va-abd-rrd-dev'

❯ kc get pods
NAME                             READY   STATUS     RESTARTS   AGE
vro-api-645dc44c64-w95mw             0/6     Init:1/3   0          7m52s
vro-api-postgres-559c5bddbb-7rm2r    1/1     Running    0          7m53s
vro-api-rabbit-mq-74bd4c5bfc-lxb7v   1/1     Running    0          7m53s
vro-api-redis-555446854-jwfqg        1/1     Running    0          7m53s

# The console container is in the pod with several containers
❯ kc exec -i -t vro-api-645dc44c64-w95mw -c abd-vro-console -- sh -c "java -jar vro-console.jar"
```

For other deployment environment, adjust the `kubectl` namespace in the alias.

More details in PR #695.

## Console Usage

### Inspect DB contents
Added in PR #531.
- On the `groovy:000>` prompt, try the following:
   ```groovy
   ?  # display help
   // Note the printJson (alias pj) custom command

   :show variables
   // Note the `claimsT` variable, which can be used to query the claims DB table

   claimsT.findAll().collect{ it.claimSubmissionId }
   c = getAnyClaim()
   // Different ways to do the same thing:
   printJson c
   printJson getAnyClaim()
   pj c
   // exit   # This will stop the container
   // Instead, press Ctrl-p Ctrl-q to detach from the container without stopping it
   ```
- See https://groovy-lang.org/groovysh.html for other built-in console commands

### Inspect Redis
Added in PR #614.
```groovy
groovy:000> :show variables
// Note the redis and redisT variables

groovy:000> redis.keys "*"
===> [claim-1234]
groovy:000> redis.hlen("claim-1234")
===> 2
groovy:000> redis.hkeys("claim-1234")
===> ["type", "pdf"]
groovy:000> redis.hget("claim-1234", "type")
===> hypertension
groovy:000> redis.hget("claim-1234", "pdf")
===> JVBERi0xLjQKMSAwIG9iago8PAovVGl0bGUgKP7/KQovQ3JlYX ... (truncated base64 encoding of the generated pdf)

// Using RedisTemplate redisT
groovy:000> ops=redisT.opsForValue()
groovy:000> ops.hget("claim-1234", "type")
===> hypertension
```

### Wiretap Camel routes
Listen to messages at certain predefined `wireTap` Camel endpoints.
Added in PR #597

```groovy
groovy:000> :show variables
// Note the camel variable

// Check out http://localhost:15672/#/queues for current queues and see how `console-*` queues are added as the following commands are run.

// Initially no routes in the CamelContext of the Console container
groovy:000> camel.routes
===> []

groovy:000> wireTap claim-submitted
// Now, submit a claim using Swagger
// Expect to see a log message

groovy:000> wireTap generate-pdf
// Now, generate a pdf using Swagger
// Expect to see a log message

groovy:000> camel.routes
===> [
  Route[rabbitmq://tap-generate-pdf?exchangeType=topic&queue=console-generate-pdf -> null],
  Route[rabbitmq://tap-claim-submitted?exchangeType=topic&queue=console-claim-submitted -> null]
]
```

### Inject message into workflow

To submit a message from the VRO Console into a Camel Route endpoint:
```groovy
// Create the request as a JSON String
req="""{
  "resourceId": "123444",
  "diagnosticCode": "A"
}"""

// Create the Camel endpoint URI
exchangeName="v3"
routingKey="postResource"
uri="rabbitmq:" + exchangeName + "?skipQueueBind=true&routingKey=" + routingKey

// Inject the message -- see CamelEntry for alternatives ways to inject
resp=pt.requestBody(uri, req, String)
===> {"resourceId":"123444","diagnosticCode":"A","status":"PROCESSING","reason":null}
```

This requires that the Camel Route endpoint be exposed outside of the JVM, e.g., the endpoint uses `rabbitmq:` and not `direct:` or `seda:`. The VRO Console has access to RabbitMQ queues, not the internal JVM queues or endpoints.

## Customizations

### Add custom console commands

See the `PrintJson` and `Wiretap` classes.

### Add custom function

Add to the `console/.../groovysh.rc` file.
