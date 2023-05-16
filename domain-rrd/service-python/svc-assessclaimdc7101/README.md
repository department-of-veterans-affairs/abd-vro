# Claims Processor for Diagnostic Code 7101 (Hypertensive vascular disease)

## Tools
[Cerberus](https://docs.python-cerberus.org/en/stable/index.html)\
[RabbitMQ](https://www.rabbitmq.com/)


## RabbitMQ configuration
The application connects to RabbitMQ with the binding key `7101`. Claims are sent to the `health-assess-exchange` and
are routed by VASRD code.

### Event object validation
Incoming messages are validated by Cerberus to avoid container shutdown from unexpected exceptions.

## VRO version 1.0
Filters and sorts health data from Lighthouse before sending it to the PDF generator.

## VRO version 2.0

Accepts aggregated Lighthouse and MAS medical data and returns both medical evidence for the PDF generator and a decision
on claim evidence sufficiency.

### Hypertension

The `assessclaimdc7101` folder contains all logic for hypertension. This service builds two queues, `health-assess.7101`
and `health-sufficiency-assess.7101`, the first of which is used in version 1.0 and the second is used in version 2.0.
