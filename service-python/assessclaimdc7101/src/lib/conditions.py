from datetime import datetime

from dateutil.relativedelta import relativedelta

from .codesets import hypertension_conditions
from .utils import extract_date, format_date


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
    conditions_two_years = []
    lh_relevant_condition_count = 0

    veterans_conditions = request_body["evidence"]["conditions"]
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])

    for condition in veterans_conditions:
        condition_code = condition["code"]
        #  Only LH data has ICD codes, so no MAS data will pass the following condition
        if condition_code in hypertension_conditions.conditions:
            if condition["category"] == "Encounter Diagnosis":
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
        try:
            condition["receiptDate"] = format_date(datetime.strptime(condition["receiptDate"], "%Y-%m-%d").date())
        except (ValueError, KeyError):
            condition["receiptDate"] = ""

    condition_with_date = sorted(
        condition_with_date,
        key=lambda i: datetime.strptime(i["recordedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    conditions_two_years = sorted(
        conditions_two_years,
        key=lambda i: datetime.strptime(i["recordedDate"], "%Y-%m-%d").date(),
        reverse=True,
    )

    condition_with_date.extend(condition_without_date)

    response.update({
        "conditions": condition_with_date,
        "conditionsTwoYears": conditions_two_years,
        "totalConditionsCount": len(veterans_conditions),
        "relevantConditionsLighthouseCount": lh_relevant_condition_count,
        }
    )
    return response
