#!/bin/bash

# This script builds BIP service and other docker containers that are associated
# with the service locally


export COMPOSE_PROFILES='platform'
source scripts/setenv.sh

./gradlew docker
./gradlew :dockerComposeUp

#./gradlew -p mocks docker
#./gradlew -p mocks :dockerComposeUp
