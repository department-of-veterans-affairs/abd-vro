from datetime import datetime

from dateutil.relativedelta import relativedelta

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
    date_of_claim = datetime.strptime(request_body["dateOfClaim"], "%Y-%m-%d").date()
    constant_sinusitis = False
    osteomyelitis = False

    for condition in veterans_conditions:
        if condition["status"].lower() in ["active", "recurrence", "relapse"]:
            condition_code = condition["code"]
            if condition_code in condition_codesets.sinusitis or condition_code in condition_codesets.rhinosinusitis:
                relevant_conditions.append(condition)
                if datetime.strptime(condition["onsetDate"], "%Y-%m-%d").date() <= date_of_claim - relativedelta(
                        months=3):
                    constant_sinusitis = True
            if condition_code in condition_codesets.osteomyelitis:
                relevant_conditions.append(condition)
                osteomyelitis = True

    response.update(
        {
            "conditions": relevant_conditions,
            "relevantConditionsCount": len(relevant_conditions),
            "totalConditionsCount": total_conditions_count,
            "constantSinusitis": constant_sinusitis,
            "osteomyelitis": osteomyelitis
        }
    )

    return response
