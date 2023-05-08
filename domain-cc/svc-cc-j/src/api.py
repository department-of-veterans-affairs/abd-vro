from fastapi import FastAPI

app = FastAPI()

from pydantic_models import ClaimForIncrease, PredictedClassification
from typing import Optional
import logging

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
)


@app.post("/classifier")
def get_classification(
    claim_for_increase: ClaimForIncrease,
) -> Optional[PredictedClassification]:
    classification = {
        "classification_code": 6602,
        "classification_name": "asthma",
    }  # replace this line w/ lookup table call
    logging.info(
        f"claim_id: {claim_for_increase.claim_id}, diagnostic_code: {claim_for_increase.diagnostic_code}, form526_submission_id: {claim_for_increase.form526_submission_id}"
    )
    logging.info(f"classification: {classification}")
    return classification
