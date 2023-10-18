#!/bin/bash
# This script is for local development or automated end2end testing.
# No production secret values are in this file.

if [[ $0 == $BASH_SOURCE ]]; then
  echo "Usage: At the root of the abd-vro folder, run:
    source scripts/setenv.sh [path/to/abd-vro-dev-secrets]"
  exit 1
fi

# Find checkout of abd-vro-dev-secrets GH repo
# If $1 is provided, use that.
# Else if VRO_DEV_SECRETS_FOLDER is set, use that.
# Else try default location but don't return error if it can't be found
#   so that GH Actions that don't rely on secrets don't fail
findSecretsDir(){
  if [ "$1" ]; then
    SECRETS_REPO_FOLDER="$1"
  elif [ "$VRO_DEV_SECRETS_FOLDER" ]; then
    SECRETS_REPO_FOLDER="$VRO_DEV_SECRETS_FOLDER"
  else
    SECRETS_REPO_FOLDER="$PWD/../abd-vro-dev-secrets"
    DEFAULT_SECRETS_REPO_FOLDER=true
  fi

  if SECRETS_DIR=$(cd -- "${SECRETS_REPO_FOLDER}/local" &> /dev/null && pwd); then
    echo "Using secrets in $SECRETS_DIR"
  elif [ "$DEFAULT_SECRETS_REPO_FOLDER" = "true" ]; then
    echo "Not loading secrets since cannot find a checkout of abd-vro-dev-secrets."
  else
    echo "Cannot find a checkout of https://github.com/department-of-veterans-affairs/abd-vro-dev-secrets!
    Expecting it to be at: $SECRETS_REPO_FOLDER.
    Alternatively, export the VRO_DEV_SECRETS_FOLDER environment variable to point to its location
    or run this script with the folder location as the first argument."
    return 11
  fi
}

findSecretsDir "$1" || return 11

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
    >&2 echo "- retrieving secret $1"
    cat "$SECRETS_DIR/$1"
  else
    >&2 echo "- not setting secret $1"
  fi
}

exportSecretIfUnset(){
  local VAR_VALUE=$(eval echo "\$$1")
  if [ "${VAR_VALUE}" ]; then
    >&2 echo "Not overriding: $1 already set."
  else
    VAR_VALUE=$(getSecret "$1")
    eval "export $1=\"$VAR_VALUE\""
  fi
  # When running in a GH Action, mask the secret
  if [ "${CI}" ] && [ "${VAR_VALUE}" ]; then
    echo "${VAR_VALUE}" | while read -r LINE; do
      echo "::add-mask::${LINE}"
    done
  fi
}

exportIfUnset(){
  local VAR_VALUE=$(eval echo "\$$1")
  if [ "${VAR_VALUE}" ]; then
    >&2 echo "Not overriding: $1 already set to: '${VAR_VALUE}'"
  else
    eval "export $1=$2"
  fi
}

exportFile(){
  local FILE_VALUE=$(eval cat "$2")
  eval "export $1=${FILE_VALUE}"
}

decodeSecretToFile(){
  if [ -f "$2" ]; then
    >&2 echo "Not overwriting file: $2 already exists."
  else
    local VAR_VALUE=$(getSecret "$1")
    if [ "$VAR_VALUE" ]; then
      >&2 echo "Creating $2"
      echo "$VAR_VALUE" | base64 -d > "$2"
    else
      >&2 echo "Not creating file $2 with empty content!"
    fi
  fi
}

###
### Settings for local development ###

# Set the prefix for container names used by docker-compose
# Not necessary, but it shortens the default prefix `abd-vro`
export COMPOSE_PROJECT_NAME=vro

# COMPOSE_PROFILES determines which containers are started by docker-compose.
# This allows developers to start only the containers they need for their current work.
# See https://docs.docker.com/compose/profiles/
# Refer to https://github.com/department-of-veterans-affairs/abd-vro/wiki/Docker-Compose#platform-base--java-app-with-platform-microservices
# For minimal VRO, set to " " (space). This prevents Platform microservices from starting.
#   (A space is used to distinguish it from an empty string, which is interpreted as
#   being unset by function exportIfUnset.)
# To start all containers, set to "all".
exportIfUnset COMPOSE_PROFILES "svc"

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
export POSTGRES_URL="postgresql://$POSTGRES_USER:$POSTGRES_PASSWORD@0.0.0.0:5432/$POSTGRES_DB"

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
# export PERSIST_TRACKING_FOLDER=/tmp/persist/tracking

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
#export MAS_API_AUTH_SCOPE=openid
export MAS_API_BASE_URL=https://viccs-api-dev.ibm-intelligent-automation.com/pca/api/dev
#export MAS_COLLECTION_ANNOTS_PATH=/pcQueryCollectionAnnots
#export MAS_COLLECTION_STATUS_PATH=/pcCheckCollectionStatus
#export MAS_CREATE_EXAM_ORDER_PATH=/pcOrderExam

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
# Credentials for BIP Claim Evidence API
export BIP_EVIDENCE_USERID=VRO_USER
export BIP_EVIDENCE_SECRET=daSecret
# BIP Common.
#export BIP_APPLICATION_ID=VRO
export BIP_STATION_ID=456

###
### Integration with BIE's Kafka ###
##
# Truststore:
# - All of the brokers can use the same truststore file.
# Keystore:
# - Each broker gets its own unique keystore.
# - For example, the FQDN of the mock Kafka broker is mock-bie-kafka
# - The alias for the keytool is set to localhost, so local connections on the broker can authenticate using SSL.
#
# To create the trust and key stores for the mock Kafka service,
# 1. Generate SSL certs using the `kafka-generate-ssl.sh` script from https://github.com/confluentinc/confluent-platform-security-tools/blob/master/kafka-generate-ssl.sh
# (Refer to "Security" section of https://hub.docker.com/r/bitnami/kafka/)
#   - When prompted to enter a password/passcode, use the same one for all.
#   - Set the Common Name or FQDN values (`mock-bie-kafka`) to your Apache Kafka container hostname...
#     Use this FQDN when prompted "What is your first and last name?"
#   - We'll use the following files created by the script:
#     - `keystore/kafka.keystore.jks`
#     - `truststore/kafka.truststore.jks`
# 3. Save the contents of the jks files as base64-encoded files in the abd-vro-dev-secrets repo and commit the changes:
#   > cat keystore/kafka.keystore.jks     | base64 > abd-vro-dev-secrets/local/BIE_KAFKA_MOCK_KEYSTORE_JKS_INBASE64
#   > cat truststore/kafka.truststore.jks | base64 > abd-vro-dev-secrets/local/BIE_KAFKA_MOCK_TRUSTSTORE_JKS_INBASE64
#   - The contents will be loaded into environment variables and saved for use by the mock BIE Kafka:
decodeSecretToFile BIE_KAFKA_MOCK_KEYSTORE_JKS_INBASE64 "mocks/mock-bie-kafka/kafka.keystore.jks"
decodeSecretToFile BIE_KAFKA_MOCK_TRUSTSTORE_JKS_INBASE64 "mocks/mock-bie-kafka/kafka.truststore.jks"

# To create certs for the Kafka microservice:
# 5. Convert jks files to p12 files:
#   > keytool -importkeystore -srckeystore kafka.truststore.jks -destkeystore truststore.p12 -srcstoretype jks -deststoretype pkcs12
#   > keytool -importkeystore -srckeystore kafka.keystore.jks   -destkeystore keystore.p12   -srcstoretype jks -deststoretype pkcs12
# 6. Save the contents of the jks files as base64-encoded files in the abd-vro-dev-secrets repo and commit the changes:
#   > cat keystore.p12     | base64 > abd-vro-dev-secrets/local/BIE_KAFKA_KEYSTORE_INBASE64
#   > cat truststore.p12 | base64 > abd-vro-dev-secrets/local/BIE_KAFKA_TRUSTSTORE_INBASE64
#   - The contents will be loaded into environment variables and saved for use by the Kafka microservice:
exportSecretIfUnset BIE_KAFKA_TRUSTSTORE_INBASE64
exportSecretIfUnset BIE_KAFKA_KEYSTORE_INBASE64
# 7. Save the passwords in the abd-vro-dev-secrets repo and commit the changes:
exportSecretIfUnset BIE_KAFKA_KEYSTORE_PASSWORD
exportSecretIfUnset BIE_KAFKA_TRUSTSTORE_PASSWORD
#   - The above 4 environment variables will referenced by svc-bie-kafka/docker-entryprep.sh and application.yaml files.
#
# 8. Ensure GitHub Action workflow `bie-kafka-end2end-test.yml`` works.
# 9. Clean up: The files created by `kafka-generate-ssl.sh` and the `p12` files can be deleted.
#    Nothing needs to be committed to the public abd-vro repo.
