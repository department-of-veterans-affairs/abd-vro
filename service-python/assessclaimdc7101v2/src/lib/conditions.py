from .codesets import hypertension_conditions


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

    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "relapse", "recurrence"]:
            condition_code = condition["code"]
            if condition_code in hypertension_conditions.conditions:
                relevant_conditions.append(condition)

    response.update({
        "conditions": relevant_conditions,
        "totalConditionsCount": len(veterans_conditions),
        "relevantConditionsCount": len(relevant_conditions)
        }
    )
    return response
