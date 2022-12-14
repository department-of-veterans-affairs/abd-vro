from datetime import datetime

from dateutil.relativedelta import relativedelta

from .codesets import (brain_cancer_condition, breast_cancer_condition,
                       gi_condition, gyn_cancer_condition,
                       head_cancer_condition, kidney_cancer_condtion,
                       male_reproductive_condition, melanoma_condition,
                       neck_condition, pancreatic_condition,
                       prostate_cancer_condition, respiratory_cancer_condition)

conditions_codeset_map = {"head": head_cancer_condition.conditions_dict,
                          "neck": neck_condition.condition_dict,
                          "male_reproductive": male_reproductive_condition.condition_dict,
                          "gyn": gyn_cancer_condition.condition_dict,
                          "prostate": prostate_cancer_condition.condition_codes,
                          "melanoma": melanoma_condition.condition_codes,
                          "pancreatic": pancreatic_condition.condition_codes,
                          "breast": breast_cancer_condition.condition_codes,
                          "gi": gi_condition.condition_codes,
                          "kidney": kidney_cancer_condtion.conditions,
                          "brain": brain_cancer_condition.conditions,
                          "respiratory": respiratory_cancer_condition.condition_dict}


# pancreatic, prostate, male reproductive, breast, gyn, gi, melanoma, kidney: condition and medication 6 months
# BRAIN: 2 years


# head: six months
# prior to the date of the claim for soft tissue sarcomas, ear cancers, eye cancers, mouth cancers, and malignant
# skin neoplasms, or within one year prior to the date of the claim for bone cancers, six months prior to the date of
# the claim) for soft tissue sarcomas, ear cancers, eye cancers, and mouth cancers, within one year prior to the date
# of the claim for bone cancers; or Veteran received a medication labeled as “systemic chemotherapy” within six
# months prior to the date of the claim for malignant skin neoplasms


# NECK: Veteran's initial cancer diagnosis date (captured in the “Condition.onset[dateTime]” element) is within six
# months prior to the date of the claim for soft tissue sarcomas, respiratory cancers, endocrine cancers,
# and malignant skin neoplasms, within one year prior to the date of the claim for bone cancers, or within two years
# prior to the date of the claim for central nervous system cancers. Veteran received a matched medication within six
# months prior to the date of the claim (i.e., “MedicationRequest.dosageInstruction.[x].timing.repeat.boundsPeriod”
# within six months prior to the date of the claim) for soft tissue sarcomas, respiratory cancers, and endocrine
# cancers, within one year prior to the date of the claim for bone cancers, or within two years prior to the date of
# the claim for central nervous system cancers; or Veteran received a medication labeled as “systemic chemotherapy”
# within six months prior to the date of the claim for malignant skin neoplasms

def categorize_condition(condition_code, condition_dict):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.

    :param condition_dict: dictionary of specific cancer type and codes
    :param condition_code: code belonging to veterans medical record
    :return: list
    """
    condition_category = str()
    for category_id in list(condition_dict.keys()):
        for cancer_condition_code in condition_dict[category_id]:
            if condition_code == cancer_condition_code:
                condition_category = category_id
                break
    return condition_category


def date_util(relevant_conditions, date_of_claim, months):
    """
    Compare date of cancer condition to the date of claim to determine if diagnosis is recent.

    :param relevant_conditions: list of relevant conditions
    :param date_of_claim: date of claim
    :param months: time constraint
    :return: True if the conditions meet ddate requirements, otherwise False
    """
    date_spec = False
    for condition in relevant_conditions:
        if datetime.strptime(condition["onsetDate"], "%Y-%m-%d").date() >= date_of_claim - relativedelta(
                months=months):
            date_spec = True

    return date_spec


def date_util_multiple_types(relevant_conditions, date_of_claim):
    """
    Compare date of cancer condition to the date of claim to determine if diagnosis is recent. For head and
    neck cancers, the type of cancers vary the date specifications.

    :param relevant_conditions: list of relevant conditions
    :param date_of_claim: date of claim
    :return: True if the conditions meet ddate requirements, otherwise False
    """
    date_spec = False
    for condition in relevant_conditions:
        if condition["suggestedCategory"] == "Bone":
            if datetime.strptime(condition["onsetDate"], "%Y-%m-%d").date() >= date_of_claim - relativedelta(
                    months=12):
                date_spec = True
        if condition["suggestedCategory"] == "Central Nervous System":
            if datetime.strptime(condition["onsetDate"], "%Y-%m-%d").date() >= date_of_claim - relativedelta(
                    months=24):
                date_spec = True
        else:
            if datetime.strptime(condition["onsetDate"], "%Y-%m-%d").date() >= date_of_claim - relativedelta(
                    months=6):
                date_spec = True

    return date_spec


def active_cancer_condition(request_body, cancer_type):
    """
    Determine if there is an active cancer diagnosis

    :param cancer_type: general area of cancer that is being adjudicated
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    relevant_conditions = []
    response = {}
    conditions_meet_date_requirements = False
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

    if cancer_type in ["pancreatic", "prostate", "male_reproductive", "breast", "gyn", "gi", "melanoma", "kidney"]:
        conditions_meet_date_requirements = date_util(relevant_conditions, date_of_claim_date, months=6)

    if cancer_type == "brain":
        conditions_meet_date_requirements = date_util(relevant_conditions, date_of_claim_date, months=24)

    if cancer_type in ["head", "neck"]:
        conditions_meet_date_requirements = date_util_multiple_types(relevant_conditions, date_of_claim_date)

    response.update({"conditions": relevant_conditions,
                     "conditionsMeetDateRequirements": conditions_meet_date_requirements,
                     "conditionsCount": len(request_body["evidence"]["conditions"]),
                     "relevantConditionsCount": len(relevant_conditions)})

    return response
