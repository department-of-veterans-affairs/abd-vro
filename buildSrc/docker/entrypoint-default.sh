#!/bin/sh
# This file is copied into the container by the Dockerfile.
# This is referenced from local.java.container-spring-conventions.gradle
# and local.python.container-service-convention.
# To run your own script, create src/docker/entrypoint.sh in your Gradle module.

# Sets environment variable secrets
[ -e set-env-secrets.src ] && . ./set-env-secrets.src

# Run entrypoint.sh if it exists, otherwise run jar file
if [ -e /app/entrypoint.sh ]; then
  exec /app/entrypoint.sh "$@"
elif [ -e "fat.jar" ]; then
  echo "Running ${JAR_FILENAME}; health check port: ${HEALTHCHECK_PORT}"
  exec java -jar fat.jar "$@"
elif [ -e "main_consumer.py" ]; then
  echo "Running main_consumer.py"
  exec python -u main_consumer.py "$@"
else
  echo ""
  ls -al
fi
