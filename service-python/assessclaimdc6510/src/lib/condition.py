from .codesets import condition_codesets


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
    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "recurrence", "relapse"]:
            condition_code = condition["code"]
            if condition_code in condition_codesets.sinusitis:
                relevant_conditions.append(condition)
                continue
            elif condition_code in condition_codesets.rhinosinusitis:
                relevant_conditions.append(condition)

    response["conditions"] = relevant_conditions
    response["relevantConditionsCount"] = len(relevant_conditions)
    response["totalConditionsCount"] = total_conditions_count

    return response
