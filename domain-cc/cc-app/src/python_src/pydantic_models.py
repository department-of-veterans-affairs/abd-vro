from typing import Optional

from pydantic import BaseModel, root_validator


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
            raise ValueError(
                "diagnostic_code is required for claim_type claim_for_increase"
            )
        return values


class PredictedClassification(BaseModel):
    """Prediction result of our ML model"""

    classification_code: Optional[int]
    classification_name: Optional[str]
    in_dropdown: bool
