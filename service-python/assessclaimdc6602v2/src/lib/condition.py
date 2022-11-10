from .codesets import condition_codesets


def conditions_calculation(request_body):
    """
    Determine if the veteran has a diagnosis of asthma
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_conditions = []

    veterans_conditions = request_body["evidence"]["conditions"]
    total_conditions_count = len(veterans_conditions)
    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "relapse", "recurrence"]:
            condition_code = condition["code"]
            if condition_code in condition_codesets.asthma_conditions:
                relevant_conditions.append(condition)
            elif condition_code in condition_codesets.persistent_asthma:
                relevant_conditions.append(condition)

    response.update(
        {
            "conditions": relevant_conditions,
            "relevantConditionsCount": len(relevant_conditions),
            "totalConditionsCount": total_conditions_count,
        }
    )

    return response
