import logging

from . import utils
from . import medication
from typing import Dict


def assess_asthma(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = utils.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        active_medications = medication.medication_required(event)
    else:
        active_medications = {"medications": [], "relevantMedCount": 0, "totalMedCount": 0}
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = "error validating request message data"

    response_body.update(
        {
            "evidence": {
                "medications": active_medications["medications"],
            },
            "evidenceSummary": {
                "relevantMedCount": active_medications["relevantMedCount"],
                "totalMedCount": active_medications["totalMedCount"]
            }
        }
    )

    return response_body
