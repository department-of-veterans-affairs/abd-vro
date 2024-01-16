#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres -v flyuser="$FLYWAY_USER" -v flypass="'$FLYWAY_PASSWORD'" -v flydb="$FLYWAY_DB" <<-EOSQL
	CREATE ROLE :flyuser PASSWORD :flypass LOGIN CREATEROLE;
	CREATE DATABASE :flydb;
	GRANT ALL PRIVILEGES ON DATABASE :flydb TO :flyuser;
EOSQL
