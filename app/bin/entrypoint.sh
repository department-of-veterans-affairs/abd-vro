#!/bin/bash
# prepare the full URL based on the hostname from AWS

exec java -jar \
  -Dspring.profiles.active=local \
  -Djava.security.egd=file:/dev/./urandom \
  $JAVA_PROFILE \
  $JAVA_OPTS \
  starter-java.jar
