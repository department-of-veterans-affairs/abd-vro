#!/bin/bash
java -version
sudo apt-get update -y
sudo apt-get install temurin-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64
export PATH=$JAVA_HOME:$PATH

./gradlew build docker -PGITHUB_ACCESS_TOKEN=$GITHUB_ACCESS_TOKEN