import logging
from typing import Dict

from . import medication, utils


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

        response_body.update(
            {
                "evidence": {
                    "medications": active_medications["medications"],
                },
                "evidenceSummary": {
                    "relevantMedCount": active_medications["relevantMedCount"],
                    "totalMedCount": active_medications["totalMedCount"],
                },
                "status": "SUCCESS"
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["status"] = "ERROR"
        response_body["errorMessage"] = "error validating request message data"

    return response_body
