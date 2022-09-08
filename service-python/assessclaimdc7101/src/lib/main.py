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
        valid_bp_readings = bp_filter.bp_recency(event)

    else:
        relevant_medication = []
        valid_bp_readings = []
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = "error validating request message data"

    response_body.update(
        {
            "evidence": {
                "medications": relevant_medication,
                "bp_readings": valid_bp_readings,
            }
        }
    )

    return response_body
