import logging
from typing import Dict

from . import condition, utils


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

    if validation_results["is_valid"]:
        conditions = condition.conditions_calculation(event)
        procedures

        response_body.update(
            {
                "evidence": {
                    "procedures": procedures["medications"],
                    "conditions": conditions["conditions"],
                },
                "evidenceSummary": {
                    "relevantMedCount": active_medications["relevantMedCount"],
                    "totalMedCount": active_medications["totalMedCount"],
                    "relevantConditionsCount": active_conditions["relevantConditionsCount"],
                    "totalConditionsCount": active_conditions["totalConditionsCount"],
                },
            }
        )
        logging.info("Message processed successfully")
    else:
        logging.info(f"Message failed to process due to: {validation_results['errors']}")
        response_body["errorMessage"] = "error validating request message data"

    return response_body
