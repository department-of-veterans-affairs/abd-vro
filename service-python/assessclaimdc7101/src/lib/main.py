from typing import Dict
import logging

from . import utils
from . import continuous_medication
from . import bp_filter


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

    else:
        relevant_medication = {"medications": [], "medicationsCount": 0}
        bp_readings = {"bpReadings": [], "totalBpReadings": 0, "recentBpReadings": 0}
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = "error validating request message data"

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

    return response_body
