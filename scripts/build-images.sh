#!/bin/bash
java -version
sudo apt-get update -y
sudo apt-get install temurin-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64
export PATH=$JAVA_HOME:$PATH

sudo wget -O /usr/local/bin/hadolint "https://github.com/hadolint/hadolint/releases/download/v2.10.0/hadolint-Linux-x86_64"
sudo chmod +x /usr/local/bin/hadolint
hadolint --version

npm install -g @stoplight/spectral-cli@6.4.0
sudo apt-get install shellcheck
which shellcheck

# Skip tests and checks since SecRel calls this script for each image it scans
./gradlew build docker -x test -x check -PGITHUB_ACCESS_TOKEN=$GITHUB_ACCESS_TOKEN
