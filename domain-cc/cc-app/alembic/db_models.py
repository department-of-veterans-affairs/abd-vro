"""Contains the SQLAlchemy models for the domain-cc schema in the database.
Changes made to the models followed by the migration commands will create a new
migration file in the versions folder.
"""

from database_config import CC_SCHEMA
from sqlalchemy import Column, Integer
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()
if CC_SCHEMA:
    Base.__table_args__ = {"schema": CC_SCHEMA}


class Claims(Base):
    __tablename__ = "claims"

    vbms_claim_id = Column(Integer, primary_key=True)


class Contentions(Base):
    __tablename__ = "contentions"

    id = Column(Integer, primary_key=True)
