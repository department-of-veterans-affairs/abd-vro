import json
import logging
import sys
import time
from functools import wraps
from typing import Tuple

from fastapi import FastAPI, HTTPException, Request

from .pydantic_models import (
    ClaimLinkInfo,
    ClassifiedContention,
    ClassifierResponse,
    Contention,
    VaGovClaim,
)
from .util.logging_dropdown_selections import build_logging_table
from .util.lookup_table import ContentionTextLookupTable, DiagnosticCodeLookupTable
from .util.sanitizer import sanitize_log

dc_lookup_table = DiagnosticCodeLookupTable()
dropdown_lookup_table = ContentionTextLookupTable()
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


@app.middleware("http")
async def save_process_time_as_metric(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    log_as_json({"process_time": process_time, "url": request.url.path})

    return response


@app.get("/health")
def get_health_status():
    if not len(dc_lookup_table):
        raise HTTPException(status_code=500, detail="Lookup table is empty")

    return {"status": "ok"}


def log_as_json(log: dict):
    if "date" not in log.keys():
        log.update({"date": time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())})
    if "level" not in log.keys():
        log.update({"level": "info"})
    logging.info(json.dumps(log))


def log_contention_stats(
    contention: Contention,
    classified_contention: ClassifiedContention,
    claim: VaGovClaim,
):
    """
    Logs stats about each contention process by the classifier. This will maintain
    compatability with the existing datadog widgets.
    """
    classification_code = classified_contention.classification_code or None
    classification_name = classified_contention.classification_name or None

    contention_text = contention.contention_text or ""
    is_in_dropdown = contention_text.strip().lower() in dropdown_values
    is_mapped_text = dropdown_lookup_table.get(contention_text, None) is not None
    log_contention_text = (
        contention_text if is_mapped_text else "unmapped contention text"
    )
    if contention.contention_type == "INCREASE":
        log_contention_type = "claim_for_increase"
    else:
        log_contention_type = contention.contention_type.lower()

    is_multi_contention = len(claim.contentions) > 1

    log_as_json(
        {
            "vagov_claim_id": sanitize_log(claim.claim_id),
            "claim_type": sanitize_log(log_contention_type),
            "classification_code": classification_code,
            "classification_name": classification_name,
            "contention_text": log_contention_text,
            "diagnostic_code": sanitize_log(contention.diagnostic_code),
            "is_in_dropdown": is_in_dropdown,
            "is_lookup_table_match": classification_code is not None,
            "is_multi_contention": is_multi_contention,
        }
    )


def log_claim_stats_v2(claim: VaGovClaim, response: ClassifierResponse):
    """
    Logs stats about each claim processed by the classifier.  This will provide
    the capability to build widgets to track metrics about claims.
    """
    log_as_json(
        {
            "claim_id": sanitize_log(claim.claim_id),
            "form526_submission_id": sanitize_log(claim.form526_submission_id),
            "is_fully_classified": response.is_fully_classified,
            "percent_clasified": (
                response.num_classified_contentions / response.num_processed_contentions
            )
            * 100,
            "num_processed_contentions": response.num_processed_contentions,
            "num_classified_contentions": response.num_classified_contentions,
        }
    )


def log_claim_stats_decorator(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        result = func(*args, **kwargs)

        if kwargs.get("claim"):
            claim = kwargs["claim"]
            log_claim_stats_v2(claim, result)

        return result

    return wrapper


def log_contention_stats_decorator(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        result = func(*args, **kwargs)
        if isinstance(args[0], Contention) and isinstance(args[1], VaGovClaim):
            contention = args[0]
            claim = args[1]
            log_contention_stats(contention, result, claim)

        return result

    return wrapper


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


def get_classification_code_name(contention: Contention) -> Tuple:
    """
    check contention type and match contention to appropriate table's
    classification code (if available)
    """
    classification_code = None
    classification_name = None
    if contention.contention_type == "INCREASE":
        classification = dc_lookup_table.get(contention.diagnostic_code)
        classification_code = classification["classification_code"]
        classification_name = classification["classification_name"]

    if contention.contention_text and not classification_code:
        classification = dropdown_lookup_table.get(contention.contention_text)
        classification_code = classification["classification_code"]
        classification_name = classification["classification_name"]

    return classification_code, classification_name


@log_contention_stats_decorator
def classify_contention(
    contention: Contention, claim: VaGovClaim
) -> ClassifiedContention:
    classification_code, classification_name = get_classification_code_name(contention)

    response = ClassifiedContention(
        classification_code=classification_code,
        classification_name=classification_name,
        diagnostic_code=contention.diagnostic_code,
        contention_type=contention.contention_type,
    )

    return response


@app.post("/va-gov-claim-classifier")
@log_claim_stats_decorator
def va_gov_claim_classifier(claim: VaGovClaim) -> ClassifierResponse:
    classified_contentions = []
    for contention in claim.contentions:
        classification = classify_contention(contention, claim)
        classified_contentions.append(classification)

    num_classified = len([c for c in classified_contentions if c.classification_code])

    response = ClassifierResponse(
        contentions=classified_contentions,
        claim_id=claim.claim_id,
        form526_submission_id=claim.form526_submission_id,
        is_fully_classified=num_classified == len(classified_contentions),
        num_processed_contentions=len(classified_contentions),
        num_classified_contentions=num_classified,
    )

    return response
