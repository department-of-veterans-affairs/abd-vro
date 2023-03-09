#!/bin/bash
# This script is for local development or automated end2end testing.
# No production secret values are in this file.

if [[ $0 == $BASH_SOURCE ]]; then
  echo "Usage: At the root of the abd-vro folder, run:
    source scripts/setenv.sh [path/to/abd-vro-dev-secrets]"
  exit 1
fi

# Find checkout of abd-vro-dev-secrets GH repo
findSecretsDir(){
  local VRO_DEV_SECRETS_FOLDER
  [ "$1" ] && VRO_DEV_SECRETS_FOLDER="$1"
  : ${VRO_DEV_SECRETS_FOLDER:=$PWD/../abd-vro-dev-secrets}
  if SECRETS_DIR=$(cd -- "${VRO_DEV_SECRETS_FOLDER}/local" && pwd); then
    echo "Using secrets in $SECRETS_DIR"
  else
    echo "Cannot find a checkout of https://github.com/department-of-veterans-affairs/abd-vro-dev-secrets!
    Expecting it to be at $VRO_DEV_SECRETS_FOLDER.
    Alternatively, export the VRO_DEV_SECRETS_FOLDER environment variable to point to its location
    or run this script with the folder location as the first argument."
    return 11
  fi
}

# $CI is set by GitHub Action -- https://docs.github.com/en/actions/learn-github-actions/environment-variables#default-environment-variables
if [ "$CI" ]; then
  echo "Relying on Action to set environment variable secrets"
else
  findSecretsDir "$1" || return 11
fi

###
# Before adding configuration settings in this file, prefer to add them to application*.yml (for Java)
# or settings*.py (for Python). Those files allow different setting values per deployment env.
# See https://github.com/department-of-veterans-affairs/abd-vro/wiki/Configuration-settings#vros-use-of-spring-profiles
# Adding environment variables incurs the cost of keeping docker-compose.yml (for local development)
# and helmcharts (for each LHDI deployment environment) updated.
#
# Reasons why a setting should be in this file:
# 1. A setting to support local development (and automated end2end testing)
# 2. Secret credentials like username, password, private token. Only fake secret values belong in this file.
# 3. A setting that must be the same or shared across containers
# When adding a setting below, also add a comment that describes the reason and where it is used.
###

echo "Setting up environment variables for VRO local development and testing"

getSecret(){
  if [ "$SECRETS_DIR" ]; then
    >&2 echo "- retrieving $1"
    cat "$SECRETS_DIR/$1"
  else
    >&2 echo "- Error: environment variable is not set: $1"
  fi
}

exportSecretIfUnset(){
  local VAR_VALUE=$(eval echo "\$$1")
  if [ "${VAR_VALUE}" ]; then
    >&2 echo "$1 already set -- not overriding."
  else
    eval "export $1=\$(getSecret $1)"
  fi
}

exportFile(){
  local FILE_VALUE=$(eval cat "$2")
  eval "export $1=${FILE_VALUE}"
}

###
### Settings for local development ###

# Set the prefix for container names used by docker-compose
# Not necessary, but it shortens the default prefix `abd-vro`
export COMPOSE_PROJECT_NAME=vro
# Determines which containers are started by docker-compose
export COMPOSE_PROFILES=assessors,feat-toggle,pdf-gen

###
### Credentials for VRO internal services ###

# Credentials for Postgres superuser (root)
export POSTGRES_SUPER_USER=vro_super_user
export POSTGRES_SUPER_PASSWORD=vro_super_user_pw

# Shared across containers to connect to Postgres
export POSTGRES_USER=vro_user
export POSTGRES_PASSWORD=vro_user_pw

# Shared across containers to use the target Postgres DB and schema
export POSTGRES_DB=vro
export POSTGRES_SCHEMA=claims

# Credentials used by Flyway to initialize VRO database
export POSTGRES_FLYWAY_USER=vro_admin_user
export POSTGRES_FLYWAY_PASSWORD=vro_admin_user_pw

# Credentials for RabbitMQ and shared across containers
export RABBITMQ_PLACEHOLDERS_USERNAME=guest
export RABBITMQ_PLACEHOLDERS_USERPASSWORD=guest

# Credentials for Redis
# Redis assumes that the implicit username is "default" -- https://redis.io/commands/auth/
export REDIS_PLACEHOLDERS_PASSWORD=vro_redis_password

# For local testing of dev and qa environments
export PERSIST_TRACKING_FOLDER=/persist/tracking

###
### Slack notifications ###

# Secret token
exportSecretIfUnset SLACK_EXCEPTION_WEBHOOK

###
### Integration with Lighthouse API ###

# Credentials for connecting to Lighthouse API
exportSecretIfUnset LH_ACCESS_CLIENT_ID
exportSecretIfUnset LH_PRIVATE_KEY

###
### Integration with MAS/IBM ###

export MAS_API_AUTH_CLIENTID=vro_dev
exportSecretIfUnset MAS_API_AUTH_CLIENT_SECRET

# TODO: Move these to application*.yml
export MAS_API_AUTH_TOKEN_URI=https://viccs-api-dev.ibm-intelligent-automation.com/pca/api/dev/token
export MAS_API_AUTH_SCOPE=openid
export MAS_API_BAS_URL=https://viccs-api-dev.ibm-intelligent-automation.com/pca/api/dev
export MAS_COLLECTION_ANNOTS_PATH=/pcQueryCollectionAnnots
export MAS_COLLECTION_STATUS_PATH=/pcCheckCollectionStatus
export MAS_CREATE_EXAM_ORDER_PATH=/pcOrderExam

###
### Integration with BIP ###
##
## Shared by app and mock-bip containers.
## I tried to move these to app/application-local (still there) but
## Gradle appears to have problems with it since these are used as a Spring
## artifact (@Value) in a different module service/provider.
## There is some discussion in stackoverflow 63846115 for solutions.
##
export BIP_CLAIM_USERID=VRO_USER
export BIP_CLAIM_SECRET=theSecret
export BIP_CLAIM_ISS=VRO
# Credentials for BIP Claim Evidence API
export BIP_EVIDENCE_USERID=VRO_USER
export BIP_EVIDENCE_SECRET=daSecret
export BIP_EVIDENCE_ISS=VRO
# BIP Common.
export BIP_APPLICATION_ID=VRO
export BIP_STATION_ID=456
