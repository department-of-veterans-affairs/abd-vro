from pydantic import BaseModel


class ClaimForIncrease(BaseModel):
    """526-ez submission w/ claim for increase on rating for existing issue"""

    diagnostic_code: int
    claim_id: int


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str
