#!/usr/bin/env bash

if [ "$DOCKER_LOGS" == 1 ]; then
  echo "Will redirect Ruby output to docker's /proc/1/fd/1"
fi
echo "Starting Ruby..."
exec bundle exec ruby main.rb
