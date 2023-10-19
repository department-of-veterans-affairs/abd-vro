from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os

DEFAULT_VRO_POSTGRES_URL = "postgresql://vro_user:vro_user_pw@0.0.0.0:5432/vro"
VRO_POSTGRES_URL = os.environ.get('POSTGRES_URL', DEFAULT_VRO_POSTGRES_URL)
dbschema = os.environ.get('POSTGRES_SCHEMA', 'claims')
engine = create_engine(
    VRO_POSTGRES_URL, connect_args={'options': '-csearch_path={}'.format(dbschema)}
)
SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=True)


def get_db():
    session = SessionLocal()
    try:
        yield session
    finally:
        session.close()
