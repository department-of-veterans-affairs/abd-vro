#!/usr/bin/env sh

# Poll to see if DB instance is ready
until pg_isready -d $POSTGRES_DB -h $POSTGRES_URL -p 5432 -U $POSTGRES_FLYWAY_USER; do
 echo 'Waiting for Postgres DB to be available'
 sleep 6
done

# Perform flyway migrations
repair && migrate -X
