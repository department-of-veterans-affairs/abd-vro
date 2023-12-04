#!/bin/bash

export COMPOSE_PROFILES='kafka'
source scripts/setenv.sh

./gradlew docker
./gradlew :dockerComposeUp
./gradlew app:dockerComposeUp
./gradlew -p mocks docker
./gradlew -p mocks :dockerComposeUp
