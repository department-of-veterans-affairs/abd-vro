import validator
import logging
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
    validation_results = validator.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        active_medications = medication.medication_required(event)
        active_conditions = condition.conditions_calculation(event)

    else:
        active_medications = []
        active_conditions = {"conditions":[], "persistent_calculation": {"succsess" : False, "mild-persistent-asthma-or-greater": False}}
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = {"errorString": "error validating request message data"}

    response_body.update({
        "evidence": {
            "medications": active_medications,
            "conditions": active_conditions["conditions"]
        },
        "calculated": {
            "persistent_calculation":active_conditions["persistant_calculation"]
        }
    })

    return response_body
    
