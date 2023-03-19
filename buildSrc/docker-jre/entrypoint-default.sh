#!/bin/sh
# This file is copied into the container by the Dockerfile.
# It is referenced from local.java.container-spring-conventions.gradle.
# To run your own script, create src/docker/entrypoint.sh in your Gradle module.

[ -e set-env.src ] && source set-env.src

# Run entrypoint.sh if it exists, otherwise run jar file
if [ -e /project/entrypoint.sh ]; then
  exec /project/entrypoint.sh "$@"
else
  echo "Running ${JAR_FILENAME}; health check port: ${HEALTHCHECK_PORT}"
  exec java -jar fat.jar "$@"
fi
