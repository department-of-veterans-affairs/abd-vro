import logging

from . import utils
from . import medication
from . import condition
from typing import Dict


def assess_sinusitis(event: Dict):
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
        active_medications = medication.medication_required(event)
        conditions = condition.conditions_calculation(event)
        response_body.update(
            {
                "evidence": {
                    "medications": active_medications,
                    "conditions": conditions
                }
            }
        )
    else:
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = "error validating request message data"

    return response_body
