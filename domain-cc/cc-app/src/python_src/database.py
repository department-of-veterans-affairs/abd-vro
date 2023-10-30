from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os
import sys

DEFAULT_VRO_POSTGRES_URL = "postgresql://vro_user:vro_user_pw@0.0.0.0:5432/vro"


def get_database_engine():
    dbschema = os.environ.get("POSTGRES_SCHEMA", "claims")
    script_name = os.path.basename(sys.argv[0])
    is_pytest = script_name in ["pytest", "py.test"]

    if is_pytest:
        database_url = "sqlite:///./test.db"
        connect_args = {"check_same_thread": False}
    else:
        database_url = os.environ.get("POSTGRES_URL", DEFAULT_VRO_POSTGRES_URL)
        connect_args = {"options": "-csearch_path={}".format(dbschema)}
    print(f'Using database_url: "{database_url}"')
    return create_engine(database_url, connect_args=connect_args)


engine = get_database_engine()
SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=True)
Base = declarative_base()


def get_db():
    session = SessionLocal()
    try:
        yield session
    finally:
        session.close()
