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
    statusCode = 200

    validation_results = utils.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        active_medication = medication.medication_required(event)

    else:
        statusCode = 400
        response_body["errors"] = validation_results["errors"]

    response_body.update({
        "medication": active_medication 
    })

    return {
        "statusCode": statusCode,
        "headers": {
            "Access-Control-Allow-Headers" : "Content-Type",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "OPTIONS,POST"
        },
        "body": json.dumps(response_body)
    }
