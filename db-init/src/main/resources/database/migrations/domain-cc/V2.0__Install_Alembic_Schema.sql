
CREATE ROLE #[alembic_username] LOGIN PASSWORD '#[alembic_password]';
GRANT CONNECT ON DATABASE #[dbname] TO #[alembic_username];

GRANT ALL PRIVILEGES ON SCHEMA #[alembic_schemaname] TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON TABLES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT SELECT, INSERT, UPDATE ON TABLES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON SEQUENCES TO #[alembic_username];
ALTER DEFAULT PRIVILEGES IN SCHEMA #[alembic_schemaname] GRANT ALL PRIVILEGES ON FUNCTIONS TO #[alembic_username];
ALTER USER #[alembic_username] SET search_path TO #[alembic_schemaname];