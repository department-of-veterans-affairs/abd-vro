from typing import Optional

from pydantic import BaseModel, root_validator
from pydantic.types import conlist


class FlattenedSingleIssueClaim(BaseModel):
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
            raise ValueError(
                "diagnostic_code is required for claim_type claim_for_increase"
            )
        return values


class Contention(BaseModel):
    contention_text: str
    diagnostic_code: Optional[int]  # only required for claim_type: "claim_for_increase"


class VaGovClaim(BaseModel):
    claim_id: int
    form526_submission_id: int
    # vbms_claim_id: int
    claim_type: str = "claim_for_increase"

    contentions: conlist(Contention, min_items=1)


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: int
    classification_name: str
