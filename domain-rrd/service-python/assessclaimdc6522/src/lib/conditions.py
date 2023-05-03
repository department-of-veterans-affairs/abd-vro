
from .codesets import rhinitis_conditions


def conditions_calculation(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_conditions = []

    veterans_conditions = request_body["evidence"]["conditions"]
    total_conditions_count = len(veterans_conditions)
    diagnostic_codes = []
    nasal_polyps = False

    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "recurrence", "relapse"]:
            condition_code = condition["code"]
            if condition_code in rhinitis_conditions.rhinitis_conditions_list:
                relevant_conditions.append(condition)
                diagnostic_codes.append("6522")
            elif condition_code in rhinitis_conditions.granulomatous_rhinitis:
                relevant_conditions.append(condition)
                diagnostic_codes.append("6524")
            elif condition_code in rhinitis_conditions.rhinoscleroma:
                relevant_conditions.append(condition)
                diagnostic_codes.append("6523")
            elif condition_code in rhinitis_conditions.nasal_polyps:
                nasal_polyps = True
                relevant_conditions.append(condition)

    response.update(
        {
            "conditions": relevant_conditions,
            "relevantConditionsCount": len(relevant_conditions),
            "totalConditionsCount": total_conditions_count,
            "diagnosticCodes": diagnostic_codes,
            "nasalPolyps": nasal_polyps
        }
    )

    return response
