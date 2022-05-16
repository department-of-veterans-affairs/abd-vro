-- "username" and "dbname" are variables that must be
-- passed in from the commandline
-- ex) psql "$POSTGRES_URL" -v username="$USERNAME" -v dbname="$DBNAME" -f init_pg.sql
CREATE USER :username WITH CREATEDB;
ALTER ROLE :username WITH CREATEROLE;
ALTER ROLE :username login password ':password';
CREATE DATABASE :username;
CREATE DATABASE :dbname;
GRANT CONNECT ON DATABASE :username TO :username;
GRANT CONNECT ON DATABASE :dbname TO :username;
GRANT ALL PRIVILEGES ON DATABASE :username TO :username;
GRANT ALL PRIVILEGES ON DATABASE :dbname TO :username;
