-- Create the schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS #[alembic_schemaname];

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '#[alembic_username]') THEN
        CREATE ROLE #[alembic_username] LOGIN PASSWORD '#[alembic_password]';
    END IF;
END
$$;

GRANT CONNECT ON DATABASE #[dbname] TO #[alembic_username];

GRANT ALL PRIVILEGES ON SCHEMA #[alembic_schemaname] TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON TABLES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT SELECT, INSERT, UPDATE ON TABLES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON SEQUENCES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON FUNCTIONS TO #[alembic_username];
ALTER USER #[alembic_username] SET search_path TO #[alembic_schemaname];
