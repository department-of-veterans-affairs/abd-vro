from typing import Optional

from fastapi import HTTPException
from pydantic import BaseModel, model_validator
from pydantic.types import conlist


class Claim(BaseModel):
    claim_id: int
    form526_submission_id: int
    diagnostic_code: Optional[int] = (
        None  # only required for claim_type: "claim_for_increase"
    )
    claim_type: str = "claim_for_increase"
    contention_text: Optional[str] = (
        None  # marked optional to retain compatibility with v1
    )

    @model_validator(mode="before")
    @classmethod
    def check_dc_for_cfi(cls, values):
        claim_type = values.get("claim_type")
        diagnostic_code = values.get("diagnostic_code")

        if claim_type == "claim_for_increase" and diagnostic_code is None:
            raise ValueError(
                "diagnostic_code is required for claim_type claim_for_increase"
            )
        return values


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str


class ClaimLinkInfo(BaseModel):
    """used for connecting VA.gov and VBMS claims to each other in order to track contention changes downstream"""

    va_gov_claim_id: int
    vbms_claim_id: int


class Contention(BaseModel):
    contention_text: str
    contention_type: str  # "disabilityActionType" in the VA.gov API
    diagnostic_code: Optional[int]  # only required for contention_type: "claim_for_increase"

    @model_validator(mode="before")
    def check_dc_for_cfi(cls, values):
        contention_type = values.get("contention_type")
        diagnostic_code = values.get("diagnostic_code")

        if contention_type == "claim_for_increase" and not diagnostic_code:
            raise HTTPException(
                422, "diagnostic_code is required for contention_type claim_for_increase"
            )


class VaGovClaim(BaseModel):
    claim_id: int
    form526_submission_id: int
    contentions: conlist(Contention, min_items=1)


class ClassifiedContention(BaseModel):
    classification_code: Optional[int]
    classification_name: Optional[str]
    diagnostic_code: Optional[int]  # only required for contention_type: "claim_for_increase"
    contention_type: str  # "disabilityActionType" in the VA.gov API


class ClassifierResponse(BaseModel):
    contentions: conlist(ClassifiedContention, min_items=1)
    claim_id: int
    form526_submission_id: int
    is_fully_classified: bool
    num_processed_contentions: int
    num_classified_contentions: int
