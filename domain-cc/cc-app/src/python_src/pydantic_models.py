from typing import Optional

from pydantic import BaseModel


class Contention(BaseModel):
    diagnostic_code: Optional[int]
    classification_code: Optional[int]

    class Config:
        orm_mode = True


class MultiContentionClasimForIncreaseSubmission(BaseModel):
    """526-ez submission w/ 1 or more contentions and claim for increase on rating for existing issue"""

    contentions: list[Contention]
    claim_id: int
    form526_submission_id: int

    class Config:
        orm_mode = True


class ClaimForIncrease(BaseModel):
    """526-ez submission w/ claim for increase on rating for existing issue"""

    diagnostic_code: int
    claim_id: int
    form526_submission_id: int

    class Config:
        orm_mode = True


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str
