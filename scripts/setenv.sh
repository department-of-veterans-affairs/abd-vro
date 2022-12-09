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
# 1. Secret credentials like username, password, private token. Only fake secret values belong in this file.
# 2. Environment variable used by a third-party Docker container (e.g., POSTGRES_PASSWORD)
# 3. A setting that must be the same or shared across containers (e.g., POSTGRES_URL)
# 4. A setting to support local development (and automated end2end testing)
# When adding a setting below, also add a comment that describes the reason and where it is used.
###

echo "Setting up environment variables for VRO local development and testing"

getSecret(){
  if [ "$SECRETS_DIR" ]; then
    >&2 echo "- using $1"
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

###
### Settings for local development ###

# Set the prefix for container names used by docker-compose
# Not necessary, but it shortens the default prefix `abd-vro`
export COMPOSE_PROJECT_NAME=vro
# Determines which containers are started by docker-compose
export COMPOSE_PROFILES=assessors,feat-toggle,pdf-gen

###
### Credentials for VRO internal services ###

# Credentials for Postgres
export POSTGRES_SUPER_USER=vro_super_user
export POSTGRES_SUPER_PASSWORD=vro_super_user_pw
export POSTGRES_SUPER_DB=vro_super_db

# Credentials for Postgres container and shared across containers
export POSTGRES_USER=vro_user
export POSTGRES_PASSWORD=vro_user_pw

# Shared across containers to connect to Postgres and use the target DB and schema
# POSTGRES_URL should match the one in docker-compose.yml
export POSTGRES_URL=jdbc:postgresql://postgres-service:5432/vro
export POSTGRES_DB=vro
export POSTGRES_SCHEMA=claims

# Credentials used by Flyway to connect to Postgres
export POSTGRES_ADMIN_USER=vro_admin_user
export POSTGRES_ADMIN_PASSWORD=vro_admin_user_pw

# TODO: Move to flyway.conf
# Only used by db-init in flyway.conf
export FLYWAY_TABLE=schema_history
export FLYWAY_TABLESPACE=pg_default

# Credentials for RabbitMQ and shared across containers
export RABBITMQ_PLACEHOLDERS_USERNAME=guest
export RABBITMQ_PLACEHOLDERS_USERPASSWORD=guest

# Credentials for Redis
# TODO NOW: How is the redis URL shared?
export REDIS_PLACEHOLDERS_PASSWORD=vro_redis_password

###
### Slack notifications ###

# Secret token
exportSecretIfUnset SLACK_EXCEPTION_WEBHOOK

###
### Integration with Lighthouse API ###

# TODO: These should be moved to application*.yml files
# Only needed by svc-lighthouse-api for connecting to Lighthouse API
export LH_TOKEN_URL=https://sandbox-api.va.gov/oauth2/health/system/v1/token
export LH_ASSERTION_URL=https://deptva-eval.okta.com/oauth2/aus8nm1q0f7VQ0a482p7/v1/token
export LH_FHIR_URL=https://sandbox-api.va.gov/services/fhir/v0/r4

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

# Credentials for BIP Claim API
exportSecretIfUnset BIP_CLAIM_USERID
exportSecretIfUnset BIP_CLAIM_SECRET
# Credentials for BIP Claim Evidence API
exportSecretIfUnset BIP_EVIDENCE_USERID
exportSecretIfUnset BIP_EVIDENCE_SECRET

# TODO: Move all? of these to application*.yml
export BIP_CLAIM_URL=2ae22533-627f-45ba-92e9-55bc55d4aae9.mock.pstmn.io
export BIP_CLAIM_ISS=virtual_regional_office
export BIP_EVIDENCE_URL=4b043eb6-1b96-45f6-9b47-7941cdf33a44.mock.pstmn.io/api/v1/rest
export BIP_EVIDENCE_ISS=VRO
export BIP_APPLICATION_ID=VRO
export BIP_STATION_ID=281
