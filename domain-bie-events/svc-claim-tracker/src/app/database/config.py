import os
from typing import Generator
from urllib.parse import quote, urlparse

from sqlmodel import Session, SQLModel, create_engine

from app.config import DEBUG


def create_sqlalchemy_db_uri() -> str:
    user = quote(os.environ.get('POSTGRES_USER') or 'vro_user')
    password = quote(os.environ.get('POSTGRES_PASSWORD') or 'vro_user_pw')
    host = os.environ.get('POSTGRES_HOST') or 'localhost'
    port = os.environ.get('POSTGRES_PORT') or '5432'
    database = os.environ.get('POSTGRES_DB') or 'vro'
    postgres_url = os.environ.get('POSTGRES_URL')

    if postgres_url is None:
        return f'postgresql://{user}:{password}@{host}:{port}/{database}'

    result = urlparse(postgres_url)
    if not result.username:
        return result._replace(netloc=f'{user}:{password}@{result.netloc}').geturl()

    return postgres_url


POSTGRES_SCHEMA = os.environ.get('POSTGRES_SCHEMA')
SQLALCHEMY_DATABASE_URI = create_sqlalchemy_db_uri()


engine = create_engine(SQLALCHEMY_DATABASE_URI, echo=DEBUG, pool_pre_ping=True)


def init_db() -> None:
    SQLModel.metadata.create_all(engine)


def get_session() -> Generator[Session, None, None]:
    """
    Yield SQLAlchemy session that manages a connection to the database
    """
    engine = create_engine(SQLALCHEMY_DATABASE_URI, echo=DEBUG, pool_pre_ping=True)
    with Session(engine) as db:
        yield db
        db.close()
