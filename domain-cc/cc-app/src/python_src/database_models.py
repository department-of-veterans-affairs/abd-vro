from datetime import datetime
from typing import List, Optional

from sqlalchemy import Column, ForeignKey, Integer
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

# from sqlalchemy.sql import func


class Claim(Base):
    __tablename__ = "claim"

    id = Column(Integer, primary_key=True, index=True)
    vets_api_claim_id = Column(Integer, unique=True, index=True)
    vets_api_form526_submission_id = Column(Integer)
    vbms_submitted_claim_id: Column(Integer, nullable=True)

    contentions = relationship("Contention", back_populates="claim")

    # @property
    # def is_claim_for_increase(self) -> bool:
    #     # TODO update to check all related objects, break if any contain diagnostic_code
    #     return self.diagnostic_code is not None


class Contention(Base):
    __tablename__ = "contentions"

    id = Column(Integer, primary_key=True, index=True)
    diagnostic_code = Column(Integer, nullable=True)
    classification_code = Column(Integer, nullable=True)

    claim_id = Column(Integer, ForeignKey("claim.id"))
    claim = relationship("Claim", back_populates="contentions")

    @property
    def is_claim_for_increase(self) -> bool:
        return self.diagnostic_code is not None
