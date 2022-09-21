-- Flyway user and db is created by postgres container initialization and can serve as admin for the db

CREATE ROLE #[username] LOGIN PASSWORD '#[userpassword]';

GRANT CONNECT ON DATABASE #[dbname] TO #[username];

GRANT USAGE ON SCHEMA #[schemaname] TO #[username];

ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT SELECT, INSERT, UPDATE ON TABLES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT EXECUTE ON FUNCTIONS TO #[username];

