import logging
from typing import Optional

from fastapi import FastAPI, HTTPException

from .pydantic_models import ClaimForIncrease, PredictedClassification
from .util.lookup_table import get_classification_name, get_lookup_table

LOOKUP_TABLE = get_lookup_table()

app = FastAPI(
    servers=[
        {
            "url": "/contention-classification",
            "description": "Contention Classification Default",
        },
    ]
)

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
)


@app.get("/health")
def get_health_status():
    if not len(LOOKUP_TABLE):
        raise HTTPException(status_code=500, detail="Lookup table is empty")

    return {"status": "ok"}



@app.post("/classifier")
def get_classification(
    claim_for_increase: ClaimForIncrease,
) -> Optional[PredictedClassification]:
    classification_code = LOOKUP_TABLE.get(claim_for_increase.diagnostic_code, None)
    if classification_code:
        classification_name = get_classification_name(classification_code)
        classification = {
            "classification_code": classification_code,
            "classification_name": classification_name,
        }
    else:
        classification = None

    logging.info(
        f"claim_id: {claim_for_increase.claim_id}, diagnostic_code: {claim_for_increase.diagnostic_code}, form526_submission_id: {claim_for_increase.form526_submission_id}"
    )
    logging.info(f"classification: {classification}")
    return classification
