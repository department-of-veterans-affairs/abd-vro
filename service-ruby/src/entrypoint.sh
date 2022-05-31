#!/usr/bin/env sh

if [ "$DOCKER_LOGS" ]; then
  echo "Will redirect Ruby output to docker's /proc/1/fd/1"
fi
echo "Starting Ruby..."
ruby microservices.rb
echo "Ruby done."
#sleep 120
echo "Exiting"
