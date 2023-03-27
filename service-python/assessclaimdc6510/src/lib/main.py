import logging
from datetime import date
from typing import Dict

import data_model

from . import condition, medication, procedure


def assess_sinusitis(event: Dict):
    """
    Take a request that includes hypertension related data, and return a response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = data_model.validate_request_body(event)

    response_body = {}
    if "dateOfClaim" not in event:
        event["dateOfClaim"] = str(date.today())

    if validation_results["is_valid"]:
        active_medications = medication.medication_required(event)
        conditions = condition.conditions_calculation(event)
        procedures = procedure.procedures_calculation(event)

        sufficient = None
        if len(conditions["conditions"]) >= 1:
            if procedures["multipleSurgery"] or conditions["constantSinusitis"] or \
                    procedures["radicalSurgery"] or conditions["osteomyelitis"]:
                sufficient = True
            else:
                sufficient = False
        response_body.update(
            {
                "evidence": {
                    "medications": active_medications["medications"],
                    "conditions": conditions["conditions"],
                    "procedures": procedures["procedures"]
                },
                "evidenceSummary": {
                    "relevantMedCount": active_medications["relevantMedCount"],
                    "totalMedCount": active_medications["totalMedCount"],
                    "relevantConditionsCount": conditions["relevantConditionsCount"],
                    "totalConditionsCount": conditions["totalConditionsCount"],
                    "relevantProceduresCount": procedures["relevantProceduresCount"],
                    "totalProceduresCount": procedures["totalProceduresCount"],
                },
                "sufficientForFastTracking": sufficient,
                "claimSubmissionId": event['claimSubmissionId']
            }
        )
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message processed successfully")
    else:
        logging.info(f"claimSubmissionId: {event['claimSubmissionId']}, message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"
        response_body["claimSubmissionId"] = event['claimSubmissionId']

    return response_body
