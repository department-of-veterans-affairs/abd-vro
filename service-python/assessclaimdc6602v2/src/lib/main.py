from typing import Dict

from . import medication, condition, utils


def assess_asthma(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

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

        response_body.update(
            {
                "evidence": {
                    "medications": active_medications,
                    "conditions": active_conditions,
                },
                "evidenceSummary": {
                    "relevantMedCount": active_medications["relevantMedCount"],
                    "totalMedCount": active_medications["totalMedCount"],
                },
            }
        )
    else:
        response_body["errorMessage"] = "error validating request message data"

    return response_body
