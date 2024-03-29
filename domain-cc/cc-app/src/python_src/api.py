import json
import logging
import sys
import time
from typing import Optional

from fastapi import FastAPI, HTTPException

from .pydantic_models import Claim, ClaimLinkInfo, PredictedClassification
from .util.brd_classification_codes import get_classification_name
from .util.logging_dropdown_selections import build_logging_table
from .util.lookup_table import ConditionDropdownLookupTable, DiagnosticCodeLookupTable
from .util.sanitizer import sanitize_log

dc_lookup_table = DiagnosticCodeLookupTable()
dropdown_lookup_table = ConditionDropdownLookupTable()
dropdown_values = build_logging_table()

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


def log_lookup_table_match(
    classification_code: int,
    contention_text: str,
):
    is_in_dropdown = contention_text.strip().lower() in dropdown_values
    log_as_json({"is_in_dropdown": sanitize_log(is_in_dropdown)})
    log_contention_text = contention_text if is_in_dropdown else "Not in dropdown"

    if classification_code:
        already_mapped_text = contention_text.strip().lower()  # do not leak PII
        log_as_json({"lookup_table_match": sanitize_log(already_mapped_text)})
    elif is_in_dropdown:
        log_as_json(
            {
                "lookup_table_match": sanitize_log(
                    f"No table match for {log_contention_text}"
                )
            }
        )
    else:
        log_as_json(
            {"lookup_table_match": sanitize_log("No table match for free text entry")}
        )


def log_as_json(log: dict):
    if "date" not in log.keys():
        log.update({"date": time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())})
    if "level" not in log.keys():
        log.update({"level": "info"})
    logging.info(json.dumps(log))


def log_claim_stats(claim: Claim, classification: Optional[PredictedClassification]):
    if classification:
        classification_code = classification.classification_code
        classification_name = classification.classification_name
    else:
        classification_code = None
        classification_name = None

    contention_text = claim.contention_text or ""
    is_in_dropdown = contention_text.strip().lower() in dropdown_values
    is_mapped_text = dropdown_lookup_table.get(contention_text, None) is not None
    log_contention_text = (
        contention_text if is_mapped_text else "unmapped contention text"
    )

    log_as_json(
        {
            "claim_id": sanitize_log(claim.claim_id),
            "claim_type": sanitize_log(claim.claim_type),
            "classification_code": classification_code,
            "classification_name": classification_name,
            "contention_text": log_contention_text,
            "diagnostic_code": sanitize_log(claim.diagnostic_code),
            "form526_submission_id": sanitize_log(claim.form526_submission_id),
            "is_in_dropdown": is_in_dropdown,
            "is_lookup_table_match": classification_code is not None,
        }
    )


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
        classification_code = dc_lookup_table.get(claim.diagnostic_code, None)

    if claim.contention_text and not classification_code:
        classification_code = dropdown_lookup_table.get(claim.contention_text, None)

    if claim.claim_type == "new":
        log_lookup_table_match(classification_code, claim.contention_text)
    else:
        log_as_json({"diagnostic code": sanitize_log(claim.diagnostic_code)})

    if classification_code:
        classification_name = get_classification_name(classification_code)
        classification = {
            "classification_code": classification_code,
            "classification_name": classification_name,
        }
    else:
        classification = None

    log_as_json({"classification": classification})
    log_claim_stats(
        claim, PredictedClassification(**classification) if classification else None
    )
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
