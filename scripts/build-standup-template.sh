#!/usr/bin/env bash

./gradlew clean
./gradlew build check docker
./gradlew :app:dockerComposeDown :app:dcPrune :app:dockerComposeUp
