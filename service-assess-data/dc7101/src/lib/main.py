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
    statusCode = 200

    validation_results = utils.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        predominance_calculation = predominant_bp.sufficient_to_autopopulate(event)
        diastolic_history_calculation = bp_history.history_of_diastolic_bp(event)
        requires_continuous_medication = continuous_medication.continuous_medication_required(event)
        predominance_calculation_status = predominance_calculation["success"]
        diastolic_history_calculation_status = diastolic_history_calculation["success"]

        # if sufficient_to_autopopulate returns 'success': False, but history_of_diastolic_bp doesn't
        # Note that the inverse can't happen (where history_of_diastolic_bp fails while sufficient_to_autopopulate doesn't)
        # because the only way history_of_diastolic_bp can fail is if there are no BP readings, which would cause
        # sufficient_to_autopopulate to fail as well

        # Additionally, there's no way for requires continuous medication to fail as well 
        if (
            (diastolic_history_calculation_status and not predominance_calculation_status) 
        ):
            statusCode = 209
        elif not predominance_calculation_status and not diastolic_history_calculation_status:
            statusCode = 400

    else:
        statusCode = 400
        predominance_calculation = {"success": False}
        diastolic_history_calculation = {"success": False}
        requires_continuous_medication = {"success": False}
        response_body["errors"] = validation_results["errors"]

    response_body.update({
        "predominance_calculation": predominance_calculation,
        "diastolic_history_calculation": diastolic_history_calculation,
        "requires_continuous_medication": requires_continuous_medication 
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
