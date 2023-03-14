import logging
from typing import Dict

import data_model

from . import medication


def assess_asthma(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)
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
                "claimSubmissionId": event['claimSubmissionId']
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
