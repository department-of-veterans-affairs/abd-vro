#!/bin/sh
# This file is copied into the container by the Dockerfile.
# It is referenced from local.python.container-service-convention.
# To run your own script, create src/docker/entrypoint.sh in your Gradle module.

# Run entrypoint.sh if it exists, otherwise run jar file
if [ -e /project/entrypoint.sh ]; then
  exec /project/entrypoint.sh "$@"
else
  echo "Running main_consumer.py"
  exec python -u main_consumer.py "$@"
fi
