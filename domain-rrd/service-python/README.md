# Claims Assessment + PDF Generator

## RabbitMQ configuration
Each folder in `service-python` contains the infrastructure necessary to build a queue using a RabbitMQ connection.


## VRO version 1
[Plan to Deploy](https://github.com/department-of-veterans-affairs/abd-vro/wiki/(March-2022)-Plan-to-Deploy-to-LHDI#vro-software)

Claims for Hypertension (7101) or Asthma (6602) are sent to their corresponding assessment service.

## VRO version 2
[VRO v2 Roadmap](https://github.com/department-of-veterans-affairs/abd-vro/wiki/VRO-v2-Roadmap#workflow-diagram)

Hypertension claims that have two data sources, MAS and Lighthouse, are evaluated for RFD.

## Request message validation
The schema validation for messages sent to `assessclaim` processors is kept in the `data_model.py` file. The message includes
an `evidence` body similar to the ABDEvidence Java object.

## VRO prototype
The folders not used in version 1.0 or 2.0 are outlines for logic to evaluate new conditions or update existing
processors. Rhinitis, sinusitis, asthma, cancer condition logic are in development. Use the Gradle flag to build the
prototype containers and set an environment variable for the docker compose configuration.

`
./gradlew -PenablePrototype build check docker
`

`
export COMPOSE_PROFILES=prototype
`


## Contributing

Tests for each service can be found in `tests/assessclaim`. Testing coverage must be 85%.
