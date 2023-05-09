from datetime import datetime

from dateutil.relativedelta import relativedelta
from utils import extract_date, format_date

from .codesets import condition_codesets


def sort_conditions(conditions):
    """
    Sort medications by 'recordedDate' date.
    :param conditions: List of conditions
    :return: Sorted list
    """

    conditions = sorted(
        conditions,
        key=lambda i: datetime.strptime(i["recordedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    return conditions


def conditions_calculation(request_body):
    """
    Determine if there is the veteran has a hypertension diagnosis
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    condition_with_date = []
    condition_without_date = []
    conditions_two_years = []
    lh_relevant_condition_count = 0

    veterans_conditions = request_body["evidence"]["conditions"]
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])

    for condition in veterans_conditions:
        condition_code = condition["code"]
        #  Only LH data has ICD codes, so no MAS data will pass the following condition
        if condition_code in condition_codesets.asthma_conditions or \
                condition_code in condition_codesets.persistent_asthma:
            condition["relevant"] = True
            lh_relevant_condition_count += 1
        else:
            condition["relevant"] = False

        try:
            condition_date = datetime.strptime(condition["recordedDate"], "%Y-%m-%d").date()
            condition["dateFormatted"] = format_date(condition_date)
            condition_with_date.append(condition)
            if condition_date >= date_of_claim_date - relativedelta(years=2):
                conditions_two_years.append(condition)
        except (ValueError, KeyError):
            condition["dateFormatted"] = ""
            condition_without_date.append(condition)

    response.update({
        "conditions": sort_conditions(condition_with_date) + condition_without_date,
        "twoYearsConditions": sort_conditions(conditions_two_years),
        "totalConditionsCount": len(veterans_conditions),
        "relevantConditionsLighthouseCount": lh_relevant_condition_count,
    }
    )
    return response
