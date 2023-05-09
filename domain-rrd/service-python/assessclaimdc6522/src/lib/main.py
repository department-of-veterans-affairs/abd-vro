import logging
from typing import Dict

import data_model

from . import conditions, medication, procedure


def assess_rhinitis(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)
    response_body = {}

    if validation_results["is_valid"]:
        active_medications = medication.medication_required(event)
        relevant_conditions = conditions.conditions_calculation(event)
        procedures = procedure.procedures_calculation(event)

        sufficient = None
        if len(relevant_conditions["conditions"]) >= 1:
            if "6523" in relevant_conditions["diagnosticCodes"] or relevant_conditions["nasalPolyps"]:
                sufficient = True
            else:
                sufficient = False
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
                "sufficientForFastTracking": sufficient,
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
