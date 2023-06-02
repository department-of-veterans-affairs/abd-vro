#!/bin/sh

# Sets environment variable secrets
[ -e set-env-secrets.src ] && . ./set-env-secrets.src

exec uvicorn python_src:api:app --reload --host 0.0.0.0 --port 8120
