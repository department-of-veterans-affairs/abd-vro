#!/bin/sh
# This file is copied into the container by the Dockerfile.
# This is referenced from local.java.container-spring-conventions.gradle
# and local.python.container-service-convention.
# To run your own script, create docker-entrypoint.sh in your Gradle module.

# Sets environment variable secrets
[ -e set-env-secrets.src ] && . ./set-env-secrets.src

# Run docker-entrypoint.sh if it exists, otherwise run jar file
if [ -e /app/docker-entrypoint.sh ]; then
  exec /app/docker-entrypoint.sh "$@"
elif [ -e "fat.jar" ]; then
  echo "Running ${JAR_FILENAME}; health check port: ${HEALTHCHECK_PORT}"
  eval exec java -jar $JAVA_OPTS fat.jar "$@"
elif [ -e "main_consumer.py" ]; then
  echo "Running: python -u main_consumer.py $@"
  exec python -u main_consumer.py "$@"
elif [ -e "main_consumer.rb" ]; then
  echo "Running: bundle exec ruby main_consumer.rb $@"
  exec bundle exec ruby main_consumer.rb "$@"
else
  echo ""
  ls -al
fi
