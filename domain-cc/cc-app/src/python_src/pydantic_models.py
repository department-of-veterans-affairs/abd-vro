from enum import StrEnum
from typing import Optional

from fastapi import HTTPException
from pydantic import BaseModel, root_validator
from pydantic.types import conlist


class Claim(BaseModel):
    claim_id: int
    form526_submission_id: int
    diagnostic_code: Optional[int]  # only required for claim_type: "claim_for_increase"
    claim_type: str = "claim_for_increase"
    contention_text: Optional[str]  # marked optional to retain compatibility with v1

    @root_validator()
    def check_dc_for_cfi(cls, values):
        claim_type = values.get("claim_type")
        diagnostic_code = values.get("diagnostic_code")

        if claim_type == "claim_for_increase" and diagnostic_code is None:
            raise HTTPException(
                "diagnostic_code is required for claim_type claim_for_increase"
            )
        return values


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str


class Contention(BaseModel):
    contention_text: str
    diagnostic_code: Optional[int]  # only required for claim_type: "claim_for_increase"


class ClaimType(StrEnum):
    claim_for_increase = "claim_for_increase"
    new = "new"


class VaGovClaim(BaseModel):
    claim_id: int
    form526_submission_id: int
    claim_type: ClaimType
    contentions: conlist(Contention, min_items=1)

    @root_validator()
    def check_dc_for_cfi(cls, values):
        claim_type = values.get("claim_type")

        if claim_type == ClaimType.claim_for_increase:
            have_diagnostic_codes = all([contention.get("diagnostic_code", None) is not None for contention in values.get("contentions")])
            if not have_diagnostic_codes:
                raise HTTPException(
                    422, "diagnostic_code is required for claim_type claim_for_increase"
                )
        return values


class ClassifierResponse(BaseModel):
    classifications: conlist(PredictedClassification, min_items=1)


class ClaimLinkInfo(BaseModel):
    """used for connecting VA.gov and VBMS claims to each other in order to track contention changes downstream"""

    va_gov_claim_id: int
    vbms_claim_id: int
