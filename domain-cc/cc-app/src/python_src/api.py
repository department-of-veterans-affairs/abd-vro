import logging
import os
import sys
import time
from typing import Optional

from datadog import initialize, statsd
from fastapi import FastAPI, HTTPException, Request

from .pydantic_models import Claim, ClaimLinkInfo, PredictedClassification
from .util.brd_classification_codes import get_classification_name
from .util.logging import log_as_json, log_lookup_table_match
from .util.lookup_table import ConditionDropdownLookupTable, DiagnosticCodeLookupTable
from .util.sanitizer import sanitize_log

options = {"statsd_host": "127.0.0.1", "statsd_port": 8125}
initialize(**options)

dc_lookup_table = DiagnosticCodeLookupTable()
dropdown_lookup_table = ConditionDropdownLookupTable()

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

env_flag = os.environ.get("ENV_FLAG", "local")


@app.middleware("http")
async def record_processing_time_in_datadog(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time_milliseconds = (time.time() - start_time) * 1000
    log_as_json({"process_time (milliseconds)": process_time_milliseconds})
    statsd.increment(
        "cc_processing_time.increment",
        tags=["environment:" + env_flag, "app:contention-classification"],
    )
    return response


logging.basicConfig(
    format="%(message)s",
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
def get_classification(claim: Claim) -> Optional[PredictedClassification]:
    log_as_json(
        {
            "claim_id": sanitize_log(claim.claim_id),
            "form526_submission_id": sanitize_log(claim.form526_submission_id),
        }
    )
    classification_code = None
    if claim.claim_type == "claim_for_increase":
        log_as_json({"diagnostic code": sanitize_log(claim.diagnostic_code)})
        classification_code = dc_lookup_table.get(claim.diagnostic_code, None)

    if claim.contention_text and not classification_code:
        classification_code = dropdown_lookup_table.get(claim.contention_text, None)

        log_lookup_table_match(classification_code, claim.contention_text)

    if classification_code:
        classification_name = get_classification_name(classification_code)
        classification = {
            "classification_code": classification_code,
            "classification_name": classification_name,
        }
    else:
        classification = None

    log_as_json({"classification": classification})
    return classification


@app.post("/claim-linker")
def link_vbms_claim_id(claim_link_info: ClaimLinkInfo):
    log_as_json(
        {
            "message": "linking claims",
            "va_gov_claim_id": sanitize_log(claim_link_info.va_gov_claim_id),
            "vbms_claim_id": sanitize_log(claim_link_info.vbms_claim_id),
        }
    )
    return {
        "success": True,
    }
