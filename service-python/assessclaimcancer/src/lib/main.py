import logging
from datetime import date
from typing import Dict

from . import condition, medication, utils


def assess_cancer(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = utils.validate_request_body(event)
    response_body = {}
    cancer_type = "head"
    if "dateOfClaim" not in event:
        event["dateOfClaim"] = str(date.today())

    if validation_results["is_valid"]:
        medications = medication.medication_match(event, cancer_type)
        conditions = condition.active_cancer_condition(event, cancer_type)

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
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
