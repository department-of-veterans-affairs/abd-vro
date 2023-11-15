#!/usr/bin/env sh

# Poll to see if DB instance is ready
until pg_isready -d $POSTGRES_DB -h $FLYWAY_URL -p 5432 -U $POSTGRES_FLYWAY_USER; do
 echo 'Waiting for Postgres DB to be available'
 sleep 5
done

# Perform flyway migrations
migrate -X
