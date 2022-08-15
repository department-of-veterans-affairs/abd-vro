import validator
import logging
from . import medication
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
        active_medication = medication.medication_required(event)


    else:
        active_medication = []
        logging.info(validation_results["errors"])
        response_body["errorMessage"] = {"errorString": "error validating request message data"}

    response_body.update({
        "evidence": {"medications": active_medication}
    })

    return response_body
    
