import logging
from typing import Dict

from . import condition, utils, procedure


def assess_respiratory_condition(event: Dict):
    """
    Take a request that includes asthma related data, and return a filtered response

    :param event: request body
    :type event: dict
    :return: response body
    :rtype: dict
    """
    validation_results = utils.validate_request_body(event)
    response_body = {}
    sufficient = None

    if validation_results["is_valid"]:
        conditions = condition.conditions_calculation(event)
        procedures = procedure.procedures_calculation(event)

        if conditions["relevantConditionsCount"] > 0:
            sufficient = False
            if "Cor pulmonale" or "Right Ventricular Hypertrophy" or "Acute Respiratory Failure" \
                    in conditions["secondaryConditions"]:
                sufficient = True
            if "Pulmonary Hypertension" in conditions["secondaryConditions"] and \
                    "Echocardiogram" or "Cardiac Catheterization" in procedures["respProcedures"]:
                sufficient = True

        response_body.update(
            {
                "evidence": {
                    "procedures": procedures["procedures"],
                    "conditions": conditions["conditions"],
                },
                "evidenceSummary": {
                    "relevantProceduresCount": procedures["relevantProceduresCount"],
                    "totalProceduresCount": procedures["totalProceduresCount"],
                    "respProcedures": procedures["respProcedures"],
                    "secondaryConditions": conditions["secondaryConditions"],
                    "relevantConditionsCount": conditions["relevantConditionsCount"],
                    "totalConditionsCount": conditions["totalConditionsCount"],
                },
                "sufficientForFastTracking": sufficient,
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
