import logging
from datetime import date
from typing import Dict

from . import condition, medication, procedure, utils


def assess_sinusitis(event: Dict):
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
        if "date_of_claim" not in event:
            event["date_of_claim"] = str(date.today())
        try:
            active_medications = medication.medication_required(event)
            conditions = condition.conditions_calculation(event)
            procedures = procedure.procedures_calculation(event)
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
                    "calculated": {
                        "radicalSurgery": procedures["radicalSurgery"],
                        "multipleSurgery": procedures["multipleSurgery"],
                        "constantSinusitis": conditions["constantSinusitis"]
                    }
                }
            )
        except Exception as e:
            logging.error(e, exc_info=True)
            response_body["errorMessage"] = str(e)
    else:
        response_body["errorMessage"] = "error validating request message data"

    return response_body
