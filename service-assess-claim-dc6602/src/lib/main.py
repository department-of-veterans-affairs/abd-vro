import json
from typing import Dict

from . import medication
from . import utils


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
        active_medication = medication.medication_required(event)

    else:
        response_body["errors"] = validation_results["errors"]

    response_body.update({
        "evidence": {"medication": active_medication}
    })

    return {
        "body": json.dumps(response_body)
    }
