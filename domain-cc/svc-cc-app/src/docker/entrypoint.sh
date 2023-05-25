#!/bin/bash

# Sets environment variable secrets
[ -e set-env-secrets.src ] && . ./set-env-secrets.src

exec python -u main_consumer.py "$@"

