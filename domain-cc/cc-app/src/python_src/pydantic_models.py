from pydantic import BaseModel, validator
from typing import Optional
from abc import ABC


class Claim(BaseModel, ABC):
    claim_id: int
    contention_text: Optional[str]  # marked optional to retain compatibility with v1
    form526_submission_id: int
    contention_text: str
    claim_type: str

    @validator('claim_type')
    def check_claim_type(cls, v):
        if v != 'claim_for_increase' and v != 'new':
            raise ValueError('claim_type must be either claim_for_increase or new')
        if v == 'claim_for_increase':
            return ClaimForIncrease
        else:
            return ClaimForNew


class ClaimForNew(Claim):
    @validator('contention_text')
    def check_contention_text(cls, v):
        if not v:
            raise ValueError('contention_text must be a string')
        return v

class ClaimForIncrease(Claim):
    """526-ez submission w/ claim for increase on rating for existing issue"""

    diagnostic_code: int
    @validator('diagnostic_code')
    def check_diagnostic_code(cls, v):
        if v < 0:
            raise ValueError('diagnostic_code must be a positive integer')
        return v


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str
