from datetime import datetime

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
        condition_code = condition["code"]
        if condition_code in hypertension_conditions.conditions:
            relevant_conditions.append(condition)

    relevant_conditions = sorted(
        relevant_conditions,
        key=lambda i: datetime.strptime(i["recordedDate"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    response.update({
        "conditions": relevant_conditions,
        "totalConditionsCount": len(veterans_conditions),
        "relevantConditionsCount": len(relevant_conditions)
        }
    )
    return response
