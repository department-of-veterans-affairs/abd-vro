import logging
import sys
from typing import Optional

from fastapi import FastAPI, HTTPException

from .pydantic_models import (
    ClaimLinkInfo,
    ClassifierResponse,
    FlattenedSingleIssueClaim,
    PredictedClassification,
    VaGovClaim,
)
from .util.brd_classification_codes import get_classification_name
from .util.logging_dropdown_selections import build_logging_table
from .util.lookup_table import ConditionDropdownLookupTable, DiagnosticCodeLookupTable

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


def do_get_classification(
    claim: FlattenedSingleIssueClaim,
) -> Optional[PredictedClassification]:
    classification_code = None
    if claim.claim_type == "claim_for_increase":
        logging.info(f"diagnostic code: {claim.diagnostic_code}")
        classification_code = dc_lookup_table.get(claim.diagnostic_code, None)

    if claim.contention_text and not classification_code:
        classification_code = dropdown_lookup_table.get(claim.contention_text, None)
        is_in_dropdown = claim.contention_text.strip().lower() in dropdown_values
        log_contention_text = (
            claim.contention_text if is_in_dropdown else "Not in dropdown"
        )
        if classification_code:
            already_mapped_text = (  # being explicit, do not leak PII
                claim.contention_text.strip().lower()
            )
            logging.info(
                f"In Dropdown: {is_in_dropdown}, "
                f" Contention Text: {log_contention_text}, Lookup table match: {already_mapped_text}"
            )
        else:
            logging.info(
                f"In Dropdown: {is_in_dropdown}, "
                f"Contention Text: {log_contention_text}, No Lookup table match"
            )

    if classification_code:
        classification_name = get_classification_name(classification_code)
        classification = {
            "classification_code": classification_code,
            "classification_name": classification_name,
        }
    else:
        classification = None

    logging.info(f"classification: {classification}")
    return classification


def generate_flattened_claim(contention_text, diagnostic_code, multi_contention_claim):
    print(f"got dc: {diagnostic_code}")
    return FlattenedSingleIssueClaim(
        claim_id=multi_contention_claim.claim_id,
        form526_submission_id=multi_contention_claim.form526_submission_id,
        diagnostic_code=diagnostic_code,
        claim_type=multi_contention_claim.claim_type,
        contention_text=contention_text,
    )


@app.post("/classifier", deprecated=True)
def classifier_v1(
    claim: FlattenedSingleIssueClaim,
) -> Optional[PredictedClassification]:
    logging.info(
        f"claim_id: {claim.claim_id}, form526_submission_id: {claim.form526_submission_id}"
    )
    return do_get_classification(claim)


@app.post("/classifier/v2")
def classifier_v2(
    multi_contention_claim: VaGovClaim,
) -> ClassifierResponse:
    contention_log_info = []
    classifications = []
    for contention in multi_contention_claim.contentions:
        contention_text = contention.contention_text
        diagnostic_code = contention.diagnostic_code

        flattened_single_issue_claim = generate_flattened_claim(
            contention_text, diagnostic_code, multi_contention_claim
        )
        classification = do_get_classification(flattened_single_issue_claim)

        is_in_dropdown = contention_text.strip().lower() in dropdown_values
        contention_text_no_pii = (
            contention_text if is_in_dropdown else "Not in dropdown"
        )
        contention_log_info.append(
            {
                "contention_text": contention_text_no_pii,
                "is_in_dropdown": is_in_dropdown,
                "is_mapped": classification is not None,
                **(classification or {}),
            }
        )
        classifications.append(classification)

    logging.info(
        f"claim_id: {multi_contention_claim.claim_id}, form526_submission_id: {multi_contention_claim.form526_submission_id}"
    )
    logging.info(f"contention_log_info: {contention_log_info}")

    return ClassifierResponse(classifications=classifications)


@app.post("/claim-linker")
def link_vbms_claim_id(claim_link_info: ClaimLinkInfo):
    logging.info(
        {
            "message": "linking claims",
            "va_gov_claim_id": claim_link_info.va_gov_claim_id,
            "vbms_claim_id": claim_link_info.vbms_claim_id,
        }
    )
    return {
        "success": True,
    }
