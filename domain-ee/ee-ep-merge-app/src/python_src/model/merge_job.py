from sqlalchemy import Column, Integer, String
from sqlalchemy.dialects.postgresql import ARRAY, JSONB, UUID
from sqlalchemy.ext.mutable import MutableDict

from db.base_class import Base


class MergeJob(Base):
    __tablename__ = "jobs"

    job_id = Column(UUID(as_uuid=True), primary_key=True, index=True)
    pending_claim_id = Column(Integer, index=True)
    ep400_claim_id = Column(Integer, index=True)
    state = Column(String, index=True)
    error_state = Column(String)
    messages = Column(ARRAY(MutableDict.as_mutable(JSONB)))
