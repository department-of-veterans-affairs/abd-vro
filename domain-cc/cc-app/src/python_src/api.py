import logging
import sys
from typing import Optional

from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy import select
from sqlalchemy.orm import Session

from . import database_models
from .database import engine, get_db
from .pydantic_models import (
    ClaimForIncrease,
    MultiContentionClasimForIncreaseSubmission,
    PredictedClassification,
)
from .util.lookup_table import get_classification_name, get_lookup_table

LOOKUP_TABLE = get_lookup_table()
database_models.Base.metadata.create_all(bind=engine)
app = FastAPI(
    title="Contention Classification",
    description="Mapping VA.gov disability form contentions to actual classifications defined in the [Benefits Reference Data API](https://developer.va.gov/explore/benefits/docs/benefits_reference_data) for use in downstream VA systems.",
    contact={"name": "Premal Shah", "email": "premal.shah@va.gov"},
    version="v0.2",
    license={
        "name": "CCO 1.0",
        "url": "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md",
    },
    servers=[
        {
            "url": "/contention-classification",
            "description": "Contention Classification Default",
        },
    ],
)

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    stream=sys.stdout,
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


def get_claim_stats(claim: list[ClaimForIncrease]) -> dict:
    claim_metrics = {
        "vets_api_claim_id": claim.claim_id,
        "vets_api_form526_submission_id": claim.form526_submission_id,
    }
    return claim_metrics


@app.post("/batch-classifier")
async def batch_classify(
    claim: MultiContentionClasimForIncreaseSubmission, db: Session = Depends(get_db)
):
    claim_metrics = get_claim_stats(claim)
    db_claim = database_models.Claim(**claim_metrics)
    db.add(db_claim)
    db.commit()
    db.refresh(db_claim)

    classifications = []
    for contention in claim.contentions:
        diagnostic_code = contention.diagnostic_code
        claim_for_increase = {
            "claim_id": claim.claim_id,
            "form526_submission_id": claim.form526_submission_id,
            "diagnostic_code": diagnostic_code,
        }
        claim_for_increase = ClaimForIncrease.parse_obj(claim_for_increase)
        classification = get_classification(claim_for_increase)
        classifications.append(classification)
        db_classification = database_models.Contention(
            claim=db_claim,
            diagnostic_code=diagnostic_code,
            classification_code=classification["classification_code"],
        )
        db.add(db_classification)
    db.commit()

    return classifications

@app.get("/test")
async def testthing(
    db: Session = Depends(get_db)
):
    result = db.execute(select(database_models.Claim).order_by(database_models.Claim.vets_api_claim_id))
    record = result.fetchone()
    # result = db.execute(select(database_models.Claim).where(database_models.Claim.vets_api_claim_id == 1))
    print(f'repr(result): {repr(record)}')
    # print(f'db result: {record.id}')
    return repr(record)