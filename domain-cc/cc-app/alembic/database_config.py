"""File sets env vars for database connection and to use in domain-cc/alembic/env.py"""
import os

username = os.environ.get("DOMAIN_CC_USER") or os.environ.get("POSTGRES_USER")
password = os.environ.get("DOMAIN_CC_PW") or os.environ.get("POSTGRES_PASSWORD")
postgres_url = os.environ.get("DB_URL")
cc_schema = os.environ.get("ALEMBIC_SCHEMA", "domain-cc")
port = 5432
env = os.environ.get("ENV", "localhost")

alembic_user = f"postgresql://{username}:{password}@{env}:{port}/vro"
print(f"databaseURL: {alembic_user}")
