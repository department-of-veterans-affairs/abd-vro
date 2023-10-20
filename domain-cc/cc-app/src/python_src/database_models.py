from sqlalchemy import Column, Integer
from .database import Base


class MasonModelNotRealModelDeleteMe(Base):
    __tablename__ = "mason_model_not_real_model_delete_me"

    id = Column(Integer, primary_key=True, index=True)
    vets_api_claim_id = Column(Integer, unique=True, index=True)
    vets_api_form526_submission_id = Column(Integer)
    vbms_submitted_claim_id: Column(Integer, nullable=True)
