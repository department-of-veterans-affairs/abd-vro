import json
from typing import Dict

from . import bp_history
from . import continuous_medication
from . import predominant_bp
from . import utils


def assess_hypertension(event: Dict):
    """
    Take a request that includes hypertension related data, and return a response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    print(event)

    validation_results = utils.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        predominance_calculation = predominant_bp.sufficient_to_autopopulate(event)
        diastolic_history_calculation = bp_history.history_of_diastolic_bp(event)
        relevant_medication = continuous_medication.continuous_medication_required(event)

    else:
        predominance_calculation = {"success": False}
        diastolic_history_calculation = {"success": False}
        relevant_medication = {}
        response_body["errors"] = validation_results["errors"]

    response_body.update(
        {"evidence": relevant_medication
        ,
        "calculated": {
            "predominance_calculation": predominance_calculation,
            "diastolic_history_calculation": diastolic_history_calculation,
    }
    })

    return {
        "body": json.dumps(response_body)
    }
