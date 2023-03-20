import logging
from datetime import date
from typing import Dict

import data_model

from . import condition, medication


def assess_cancer(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)
    response_body = {}
    cancer_type = "respiratory"
    if "dateOfClaim" not in event:
        event["dateOfClaim"] = str(date.today())

    if validation_results["is_valid"]:
        medications = medication.medication_match(event, cancer_type)
        conditions = condition.active_cancer_condition(event, cancer_type)
        sufficient = None

        if conditions["relevantConditionsCount"] > 0:
            if any({medications["medicationMeetsDateRequirements"], conditions["conditionsMeetDateRequirements"]}):
                sufficient = True  # Proceed with fast track
            else:
                sufficient = False  # Order an exam

        response_body.update(
            {
                "evidence": {
                    "medications": medications["medications"],
                    "conditions": conditions["conditions"]
                },
                "evidenceSummary": {
                    "relevantMedCount": medications["relevantMedCount"],
                    "totalMedCount": medications["totalMedCount"],
                    "conditionsCount": conditions["conditionsCount"],
                    "relevantConditionsCount": conditions["relevantConditionsCount"]
                },
                "sufficientForFastTracking": sufficient,
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
