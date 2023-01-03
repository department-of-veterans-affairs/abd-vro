# Claims Processor for Diagnostic Code 7101 (Hypertensive vascular disease)

## Tools
[Cerberus](https://docs.python-cerberus.org/en/stable/index.html)\
[RabbitMQ](https://www.rabbitmq.com/)

## RabbitMQ configuration
The application connects to RabbitMQ with the binding key `7101`. Claims are sent to the `health-assess-exchange` and are routed by VASRD code.

### Event object validation
Incoming messages are validated by Cerberus to avoid container shutdown from unexpected exceptions.

### VRO version 1.0

MedicationRequest objects are evaluated by keyword matching. If any of the identified keywords appear in the medicationRequest object description, they are collected as evidence.

Blood pressure readings are filtered upstream to be <1 year from the date of claim.

Evidence under `calculated` in the response object is still under development and is not currently in use.

## VRO version 2.0

Accepts aggregated Lighthouse and MAS medical data and returns both medical evidence for the PDF generator and a decision
on claim evidence sufficiency.

### Hypertension

The `assessclaimdc7101` folder contains all logic for hypertension. This service builds two queues built, "health-assess.7101"
and "health-sufficiency-assess.7101", the first of which is used in version 1.0 and the second is used in version 2.0. 


## VRO prototype
The folders not used in version 1.0 or 2.0 are outlines for logic to evaluate new conditions or update existing 
processors.


### Rhinitis, Sinusitis

### Asthma

### Cancer

Tests for each service can be found in `tests/assessclaim`

## Contributing