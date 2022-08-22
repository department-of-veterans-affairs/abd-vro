import logging

from . import utils
from . import medication
from . import condition
from typing import Dict


def assess_asthma(event: Dict):
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
        active_conditions = condition.conditions_calculation(event)

    else:
        active_medications = []
        active_conditions = {"conditions":[], "persistent_calculation": {"success" : False, "mild-persistent-asthma-or-greater": False}}
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = "error validating request message data"

    response_body.update({
        "evidence": {
            "medications": active_medications,
            "conditions": active_conditions["conditions"]
        },
        "calculated": {
            "persistent_calculation": active_conditions["persistent_calculation"]
        }
    })

    return response_body
    
