from datetime import datetime
from assessclaimcancer.src.lib.codesets import male_reproductive_condition, melanoma_condition, \
    head_cancer_condition, gyn_cancer_condition, pancreatic_condition, prostate_cancer_condition, neck_condition

conditions_codeset_map = {"head": head_cancer_condition.conditions_dict,
                          "neck": neck_condition.condition_dict,
                          "male_reproductive": male_reproductive_condition.condition_dict,
                          "gyn": gyn_cancer_condition.condition_dict,
                          "prostate": prostate_cancer_condition.condition_codes,
                          "melanoma": melanoma_condition.condition_codes,
                          "pancreatic": pancreatic_condition.condition_codes}


def categorize_condition(condition_code, condition_dict):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.

    :param medication_dict:
    :param medication_display: medication text
    :return: list
    """
    condition_category = str()
    for category_id in list(condition_dict.keys()):
        for cancer_condition_code in condition_dict[category_id]:
            if condition_code == cancer_condition_code:
                condition_category = category_id
                break
    return condition_category


def active_cancer_condition(request_body, cancer_type):
    """
    Determine if there is an active pancreatic cancer diagnosis

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    relevant_conditions = []
    response = {}
    date_of_claim = request_body["dateOfClaim"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    condition_codes = conditions_codeset_map[cancer_type]

    if type(condition_codes) == dict:
        for condition in request_body["evidence"]["conditions"]:

            condition_code = condition["code"]
            condition_category = categorize_condition(condition_code, condition_codes)
            if condition_category:
                condition["suggestedCategory"] = condition_category
                relevant_conditions.append(condition)

    if type(condition_codes) == set:
        for condition in request_body["evidence"]["conditions"]:
            condition_code = condition["code"]
            for cancer_condition_code in condition_codes:
                if cancer_condition_code == condition_code:
                    relevant_conditions.append(condition)

    response.update({"conditions": relevant_conditions,
                     "conditionsCount": len(request_body["evidence"]["conditions"]),
                     "relevantConditionsCount": len(relevant_conditions)})

    return response
