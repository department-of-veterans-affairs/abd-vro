from .codesets import condition_codesets


def conditions_calculation(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    relevant_conditions = []

    veterans_conditions = request_body["evidence"]["conditions"]
    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "recurrence", "relapse"]:
            condition_code = condition["code"]
            if condition_code in condition_codesets.sinusitis:
                relevant_conditions.append(condition)
                break
            elif condition_code in condition_codesets.rhinosinusitis:
                relevant_conditions.append(condition)

    return relevant_conditions
