from sqlalchemy import Column, Integer
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


class Claims(Base):
    __tablename__ = "claims"
    # __table_args__ = {'schema': 'domain-cc'}

    vbms_claim_id = Column(Integer, primary_key=True)


class Contentions(Base):
    __tablename__ = "contentions"
    # __table_args__ = {'schema': 'domain-cc'}

    id = Column(Integer, primary_key=True)
