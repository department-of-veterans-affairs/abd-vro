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
    condition_with_date = []
    condition_without_date = []
    count = 0

    veterans_conditions = request_body["evidence"]["conditions"]

    for condition in veterans_conditions:
        condition_code = condition["code"]
        if "recordedDate" in condition.keys():
            date = datetime.strptime(condition["recordedDate"], "%Y-%m-%d").date()
            condition["dateFormatted"] = date.strftime("%m/%d/%Y")
            condition_with_date.append(condition)

        else:
            condition_without_date.append(condition)

        if condition_code in hypertension_conditions.conditions:
            condition["relevant"] = True
            count += 1
        else:
            condition["relevant"] = False

    condition_with_date = sorted(
        condition_with_date,
        key=lambda i: datetime.strptime(i["recordedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    condition_with_date.extend(condition_without_date)

    response.update({
        "conditions": condition_with_date,
        "totalConditionsCount": len(veterans_conditions),
        "relevantConditionsCount": count
        }
    )
    return response
