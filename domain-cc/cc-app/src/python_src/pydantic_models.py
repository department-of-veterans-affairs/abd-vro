from typing import Optional

from pydantic import BaseModel, model_validator


class Claim(BaseModel):
    claim_id: int
    form526_submission_id: int
    diagnostic_code: Optional[int]  # only required for claim_type: "claim_for_increase"
    claim_type: str = "claim_for_increase"
    contention_text: Optional[str]  # marked optional to retain compatibility with v1

    @model_validator
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
