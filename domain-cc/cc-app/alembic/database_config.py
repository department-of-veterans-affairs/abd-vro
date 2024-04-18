"""File sets env vars for database connection and to use in domain-cc/alembic/env.py"""
import os

DB_USERNAME = os.environ.get("DOMAIN_CC_USER") or os.environ.get("POSTGRES_USER")
DB_PASSWORD = os.environ.get("DOMAIN_CC_PW") or os.environ.get("POSTGRES_PASSWORD")
DB_URL = os.environ.get("DB_URL")
CC_SCHEMA = os.environ.get("ALEMBIC_SCHEMA")
DB_PORT = 5432
ENV = os.environ.get("ENV", "localhost")

alembic_user = f"postgresql://{DB_USERNAME}:{DB_PASSWORD}@{ENV}:{DB_PORT}/vro"
