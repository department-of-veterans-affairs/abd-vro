from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

VRO_POSTGRES_URL = "postgres://vro_user:vro_user_pw@localhost:5432/vro"
# POSTGRES_DATABASE_URL = "postgresql://postgres:postgres@localhost:5432/postgres"
# SQLITE_DATABASE_URL = "sqlite:///./sql_app.db"
# engine = create_engine(SQLITE_DATABASE_URL, connect_args={"check_same_thread": False})
# engine = create_engine(POSTGRES_DATABASE_URL)
engine = create_engine(VRO_POSTGRES_URL)
SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=True)
Base = declarative_base()


def get_db():
    session = SessionLocal()
    try:
        yield session
    finally:
        session.close()
