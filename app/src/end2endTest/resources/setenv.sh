#!/bin/sh
echo "Setting up environment variables for end2end test"

export COMPOSE_PROJECT_NAME=vro

#export LH_ACCESS_CLIENT_ID=${{ secrets.LH_ACCESS_CLIENT_ID }}
export LH_TOKEN_URL=https://sandbox-api.va.gov/oauth2/health/system/v1/token
export LH_ASSERTION_URL=https://deptva-eval.okta.com/oauth2/aus8nm1q0f7VQ0a482p7/v1/token
export LH_FHIR_URL=https://sandbox-api.va.gov/services/fhir/v0/r4
#export LH_PRIVATE_KEY=${{ secrets.LH_PRIVATE_KEY }}

export POSTGRES_SUPER_USER=vro_super_user
export POSTGRES_SUPER_PASSWORD=vro_super_user_pw
export POSTGRES_SUPER_DB=vro_super_db

export POSTGRES_ADMIN_USER=vro_admin_user
export POSTGRES_ADMIN_PASSWORD=vro_admin_user_pw

export FLYWAY_TABLE=schema_history
export FLYWAY_TABLESPACE=pg_default

export RABBITMQ_PLACEHOLDERS_USERNAME=guest
export RABBITMQ_PLACEHOLDERS_USERPASSWORD=guest

export POSTGRES_USER=vro_user
export POSTGRES_PASSWORD=vro_user_pw
export POSTGRES_DB=vro
# POSTGRES_URL should match the one in app/.../docker-compose.yml
export POSTGRES_URL=jdbc:postgresql://postgres-service:5432/vro
export POSTGRES_SCHEMA=claims

export REDIS_PLACEHOLDERS_PASSWORD=vro_redis_password

export MAS_API_AUTH_CLIENTID=bogusClientId
export MAS_API_AUTH_CLIENT_SECRET=bogusClientSecret
export MAS_API_AUTH_TOKEN_URI=bogusToken
export MAS_API_AUTH_SCOPE=openid



# Set this in your env to enable the VRO Console container
export COMPOSE_PROFILES=assessors,feat-toggle,pdf-gen

#export SLACK_EXCEPTION_WEBHOOK=${{ secrets.SLACK_EXCPTN_WEBHOOK_NONPROD }}
