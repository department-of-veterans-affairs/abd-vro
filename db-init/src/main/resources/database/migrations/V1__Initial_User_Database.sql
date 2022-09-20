-- Flyway user and db is created by postgres container initialization and can serve as admin for the db

CREATE USER #[username];
ALTER ROLE #[username] login password '#[userpassword]';
GRANT CONNECT ON DATABASE #[dbname] TO #[username];
GRANT ALL PRIVILEGES ON DATABASE #[dbname] TO #[username];
GRANT USAGE ON SCHEMA #[schemaname] TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT SELECT ON TABLES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT SELECT ON SEQUENCES TO #[username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[schemaname] GRANT EXECUTE ON FUNCTIONS TO #[username];

