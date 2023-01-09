from datetime import datetime

from .codesets import emphysema_condition_codesets, bronchitis_condition_codesets, copd_condition_codesets, secondary_condition_codesets


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
            if condition_code in bronchitis_condition_codesets.bronchitis_conditions:
                relevant_conditions.append(condition)
            if condition_code in copd_condition_codesets.copd_conditions:
                relevant_conditions.append(condition)
            if condition_code in emphysema_condition_codesets.emphysema_conditions:
                relevant_conditions.append(condition)
            if condition_code in secondary_condition_codesets.secondary_diagnosis:
                relevant_conditions.append(condition)

            try:
                date = datetime.strptime(condition["recordedDate"], "%Y-%m-%d").date()
                condition["dateFormatted"] = date.strftime("%m/%d/%Y")
                condition_with_date.append(condition)
            except ValueError:
                condition["dateFormatted"] = ""
                condition_without_date.append(condition)

    response.update(
        {
            "conditions": relevant_conditions,
            "relevantConditionsCount": len(relevant_conditions),
            "totalConditionsCount": total_conditions_count,
        }
    )

    return response
