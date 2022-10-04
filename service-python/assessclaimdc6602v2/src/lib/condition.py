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
        if condition["status"].lower() == "active":
            condition_code = condition["code"]
            if condition_code in condition_codesets.asthma_conditions:
                relevant_conditions.append(condition)
            elif condition_code in condition_codesets.persistent_asthma:
                relevant_conditions.append(condition)

    return relevant_conditions
