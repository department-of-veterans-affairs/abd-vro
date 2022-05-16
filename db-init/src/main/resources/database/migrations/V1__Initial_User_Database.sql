-- "{user,service,admin}{name,password}" and "dbname" are variables that must be
-- passed in from the commandline or provided in flyway.conf
--

-- This is currently created by postgres container initialization
-- CREATE DATABASE #[dbname];

CREATE USER #[adminname] WITH CREATEDB;
ALTER ROLE #[adminname] WITH CREATEROLE;
ALTER ROLE #[adminname] login password '#[adminpassword]';

CREATE DATABASE #[adminname];
GRANT ALL PRIVILEGES ON DATABASE #[adminname] TO #[adminname];
GRANT CONNECT ON DATABASE #[adminname] TO #[adminname];
GRANT CONNECT ON DATABASE #[dbname] TO #[adminname];
GRANT ALL PRIVILEGES ON DATABASE #[dbname] TO #[adminname];
GRANT USAGE ON SCHEMA #[schemaname] TO #[adminname];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON TABLES TO #[adminname];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON SEQUENCES TO #[adminname];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON FUNCTIONS TO #[adminname];


CREATE USER #[username];
ALTER ROLE #[username] login password '#[userpassword]';
GRANT CONNECT ON DATABASE #[dbname] TO #[username];
GRANT ALL PRIVILEGES ON DATABASE #[dbname] TO #[username];
GRANT USAGE ON SCHEMA #[schemaname] TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT SELECT ON TABLES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT SELECT ON SEQUENCES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT EXECUTE ON FUNCTIONS TO #[username];

CREATE USER #[servicename];
ALTER ROLE #[servicename] login password '#[servicepassword]';
GRANT CONNECT ON DATABASE #[dbname] TO #[servicename];
GRANT ALL PRIVILEGES ON DATABASE #[dbname] TO #[servicename];
GRANT USAGE ON SCHEMA #[schemaname] TO #[servicename];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON TABLES TO #[servicename];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON SEQUENCES TO #[servicename];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT ALL PRIVILEGES ON FUNCTIONS TO #[servicename];

-- not sure if this is necessary
GRANT ALL PRIVILEGES ON DATABASE #[adminname] TO #[flywayname];
GRANT CONNECT ON DATABASE #[adminname] TO #[flywayname];

