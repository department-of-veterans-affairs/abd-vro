import logging
import sys
from typing import Optional

from fastapi import FastAPI, HTTPException
from .pydantic_models import Claim, ClaimForIncrease, PredictedClassification
from .util.lookup_table import DropdownLookupTable, DiagnosticCodeLookupTable
from .util.brd_classification_codes import get_classification_name

dc_lookup_table = DiagnosticCodeLookupTable()
dropdown_lookup_table = DropdownLookupTable()


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
    if not len(dc_lookup_table):
        raise HTTPException(status_code=500, detail="Lookup table is empty")

    return {"status": "ok"}


@app.post("/classifier")
def get_classification(
    claim_for_increase: ClaimForIncrease,
) -> Optional[PredictedClassification]:
    classification_code = dc_lookup_table.get(claim_for_increase.diagnostic_code, None)
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


@app.post("/v2/classifier")
def get_classification_v2(claim: Claim) -> Optional[PredictedClassification]:
    classification_code = None
    if isinstance(claim, ClaimForIncrease):
        classification_code = dc_lookup_table.get(claim.diagnostic_code, None)

    if not classification_code:
        classification_code = dropdown_lookup_table.get(claim.contention_text, None)

    if classification_code:
        classification_name = get_classification_name(classification_code)
        classification = {
            "classification_code": classification_code,
            "classification_name": classification_name,
        }
    else:
        classification = None

    if isinstance(claim, ClaimForIncrease):
        logging.info(f"claim_id: {claim.claim_id}, diagnostic_code: {claim.diagnostic_code}, form526_submission_id: {claim.form526_submission_id}")
    else:
        logging.info(
            f"claim_id: {claim.claim_id}, form526_submission_id: {claim.form526_submission_id}")
    logging.info(f"classification: {classification}")

    return classification
