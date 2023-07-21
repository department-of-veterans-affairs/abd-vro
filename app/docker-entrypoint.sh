#!/bin/sh
# prepare the full URL based on the hostname from AWS

eval exec java -jar \
  -Djava.security.egd=file:/dev/./urandom \
  $JAVA_PROFILE \
  $JAVA_OPTS \
  fat.jar
