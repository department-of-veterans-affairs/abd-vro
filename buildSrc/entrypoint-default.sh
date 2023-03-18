#!/bin/sh
# Run entrypoint.sh if it exists, otherwise run jar file
if [ -e /project/entrypoint.sh ]; then
  exec /project/entrypoint.sh "$@"
else
  echo "Running ${JAR_FILENAME}; health check port: ${HEALTHCHECK_PORT}"
  exec java -jar fat.jar "$@"
fi
