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
    secondary_condition_present = []
    active_oxygen_therapy = False

    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "relapse", "recurrence"]:
            condition_code = condition["code"]
            if condition_code in bronchitis_condition_codesets.bronchitis_conditions:
                relevant_conditions.append(condition)
            if condition_code in copd_condition_codesets.copd_conditions:
                relevant_conditions.append(condition)
            if condition_code in emphysema_condition_codesets.emphysema_conditions:
                relevant_conditions.append(condition)

            for category_id in list(secondary_condition_codesets.secondary_diagnosis.keys()):
                if condition_code in secondary_condition_codesets.secondary_diagnosis[category_id]:
                    secondary_condition_present.append(category_id)
                    relevant_conditions.append(condition)
                    if category_id == "Oxygen Therapy" and condition["performedDate"]:
                        active_oxygen_therapy = True
                    break

    response.update(
        {
            "conditions": relevant_conditions,
            "relevantConditionsCount": len(relevant_conditions),
            "totalConditionsCount": total_conditions_count,
            "secondaryConditions": secondary_condition_present,
            "oxygenTherapy": active_oxygen_therapy
        }
    )

    return response
