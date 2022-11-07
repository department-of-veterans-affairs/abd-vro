import logging
from typing import Dict

from . import bp_filter, continuous_medication, utils


def assess_hypertension(event: Dict):
    """
    Take a request that includes hypertension related data, and return a response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """

    validation_results = utils.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        relevant_medication = continuous_medication.continuous_medication_required(
            event
        )
        bp_readings = bp_filter.bp_recency(event)
        response_body.update(
            {
                "evidence": {
                    "medications": relevant_medication["medications"],
                    "bp_readings": bp_readings["bpReadings"],
                },
                "evidenceSummary": {
                    "totalBpReadings": bp_readings["totalBpReadings"],
                    "recentBpReadings": bp_readings["recentBpReadings"],
                    "medicationsCount": relevant_medication["medicationsCount"],
                },
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
