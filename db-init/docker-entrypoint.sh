#!/usr/bin/env sh

# Assuming POSTGRES_URL is passed in without the jdbc: prefix
# and construct FLYWAY_URL with the prefix.
FLYWAY_URL="jdbc:$POSTGRES_URL"

# Poll to see if DB instance is ready
until pg_isready -d $POSTGRES_DB -h $POSTGRES_URL -p 5432 -U $POSTGRES_FLYWAY_USER; do
 echo 'Waiting for Postgres DB to be available'
 sleep 5
done

# Perform flyway migrations
migrate -X
