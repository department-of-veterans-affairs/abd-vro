import uuid
from datetime import datetime

from sqlmodel import Field, SQLModel

from app.database.config import POSTGRES_SCHEMA


class TrackedClaimBase(SQLModel):
    """Create a base model for a tracked claim."""

    claim_id: int = Field(index=True)
    established_at: datetime
    feature_name: str
    feature_enabled: bool


class TrackedClaim(TrackedClaimBase, table=True):
    """Create a model for a tracked claim."""

    __tablename__ = 'tracked_claims'
    __table_args__ = {'schema': POSTGRES_SCHEMA}

    id: uuid.UUID = Field(default_factory=uuid.uuid4, primary_key=True, index=True)
    created_at: datetime = Field(default_factory=datetime.utcnow)
