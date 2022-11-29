from typing import Dict

from . import conditions, medication, procedure, utils


def assess_rhinitis(event: Dict):
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
        relevant_conditions = conditions.conditions_calculation(event)
        procedures = procedure.procedures_calculation(event)

        response_body.update(
            {
                "evidence": {
                    "medications": active_medications["medications"],
                    "conditions": relevant_conditions["conditions"],
                    "procedures": procedures["procedures"]
                },
                "evidenceSummary": {
                    "relevantMedCount": active_medications["relevantMedCount"],
                    "totalMedCount": active_medications["totalMedCount"],
                    "relevantConditionsCount": relevant_conditions["relevantConditionsCount"],
                    "totalConditionsCount": relevant_conditions["totalConditionsCount"],
                    "diagnosticCodes": relevant_conditions["diagnosticCodes"],
                    "relevantProceduresCount": procedures["relevantProceduresCount"],
                    "totalProceduresCount": procedures["totalProceduresCount"],
                },
            }
        )
    else:
        response_body["errorMessage"] = "error validating request message data"

    return response_body
