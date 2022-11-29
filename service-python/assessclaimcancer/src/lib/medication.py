from datetime import datetime

from dateutil.relativedelta import relativedelta

from .codesets import (brain_cancer_medication, breast_cancer_medication,
                       gi_medication, gyn_cancer_medication,
                       head_cancer_medication, kidney_cancer_medication,
                       male_reproductive_medication, melanoma_medication,
                       neck_medication, pancreatic_medication,
                       prostate_cancer_medication,
                       respiratory_cancer_medication)

medication_codeset_map = {"head": head_cancer_medication.medication_keywords,
                          "neck": neck_medication.medications,
                          "male_reproductive": male_reproductive_medication.medications,
                          "gyn": gyn_cancer_medication.medications,
                          "prostate": prostate_cancer_medication.medication_keywords,
                          "melanoma": melanoma_medication.medications,
                          "pancreatic": pancreatic_medication.medication_keywords,
                          "breast": breast_cancer_medication.medication_keywords,
                          "gi": gi_medication.medications,
                          "kidney": kidney_cancer_medication.medications,
                          "brain": brain_cancer_medication.medications,
                          "respiratory": respiratory_cancer_medication.medications}


def categorize_med(medication_display, medication_dict):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.

    :param medication_dict:
    :param medication_display: medication text
    :return: list
    """
    medication_category = str()
    for category_id in list(medication_dict.keys()):
        for medication in medication_dict[category_id]:
            if medication.lower() in medication_display.lower():
                medication_category = category_id
                break
    return medication_category


def date_util(relevant_medications, date_of_claim, months):
    """
    Compare date of cancer condition to the date of claim to determine if diagnosis is recent.

    :param relevant_medications: list of relevant conditions
    :param date_of_claim: date of claim
    :param months: time constraint
    :return: True if the conditions meet ddate requirements, otherwise False
    """
    date_spec = False
    for medication in relevant_medications:
        if datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date() >= date_of_claim - relativedelta(
                months=months):
            date_spec = True

    return date_spec


def date_util_multiple_types(relevant_medications, date_of_claim):
    """
    Compare date of cancer condition to the date of claim to determine if diagnosis is recent. For head and
    neck cancers, the type of cancers vary the date specifications.

    :param relevant_medications: list of relevant conditions
    :param date_of_claim: date of claim
    :return: True if the conditions meet ddate requirements, otherwise False
    """
    date_spec = False
    for medication in relevant_medications:
        if "Bone" in medication["suggestedCategory"]:
            if datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date() >= date_of_claim - relativedelta(
                    months=12):
                date_spec = True
        if "Central Nervous System" in medication["suggestedCategory"]:
            if datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date() >= date_of_claim - relativedelta(
                    months=24):
                date_spec = True
        else:
            if datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date() >= date_of_claim - relativedelta(
                    months=6):
                date_spec = True

    return date_spec


def medication_match(request_body, cancer_type):
    """
    Determine if there is the veteran requires continuous medication for cancer

    :param cancer_type:
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_medications = []
    medication_meets_date_requirements = False
    date_of_claim = request_body["dateOfClaim"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    medication_keywords = medication_codeset_map[cancer_type]

    if type(medication_keywords) == dict:
        for medication in request_body["evidence"]["medications"]:
            medication_display = medication["description"]
            medication_category = categorize_med(medication_display, medication_keywords)
            if medication_category:
                medication["conditionRelated"] = True
                medication["suggestedCategory"] = medication_category
                relevant_medications.append(medication)

    if type(medication_keywords) == set:
        for medication in request_body["evidence"]["medications"]:
            medication_display = medication["description"]
            for med in [x.lower() for x in medication_keywords]:
                if med in medication_display.lower():
                    medication["conditionRelated"] = True
                    relevant_medications.append(medication)
                    break

    if cancer_type in ["pancreatic", "prostate", "male_reproductive", "breast", "gyn", "gi", "melanoma", "kidney"]:
        medication_meets_date_requirements = date_util(relevant_medications, date_of_claim_date, months=6)
    if cancer_type in ["neck", "head"]:
        medication_meets_date_requirements = date_util_multiple_types(relevant_medications, date_of_claim_date)

    relevant_medications = sorted(
        relevant_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )
    response.update({
        "medications": relevant_medications,
        "medicationMeetsDateRequirements": medication_meets_date_requirements,
        "relevantMedCount": len(relevant_medications),
        "totalMedCount": len(request_body["evidence"]["medications"])})
    return response
