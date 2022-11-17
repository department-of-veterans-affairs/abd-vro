from datetime import datetime
from assessclaimcancer.src.lib.codesets import male_reproductive_medication, melanoma_medication, \
    head_cancer_medication, gyn_cancer_medication, pancreatic_medication, prostate_cancer_medication, neck_medication


# not one to one with VASRD
# Head, Neck, Respiratory, GI, Reproductive, Kidney, Brain, Melanoma, Pancreatic, Prostate Cancer
medication_codeset_map = {"head": head_cancer_medication.medication_keywords,
                          "neck": neck_medication.medications,
                          "male_reproductive": male_reproductive_medication.medications,
                          "gyn": gyn_cancer_medication.medications,
                          "prostate": prostate_cancer_medication.medication_keywords,
                          "melanoma": melanoma_medication.medications,
                          "pancreatic": pancreatic_medication.medication_keywords}


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
    date_of_claim = request_body["dateOfClaim"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    medication_keywords = medication_codeset_map[cancer_type]

    if type(medication_keywords) == dict:
        for medication in request_body["evidence"]["medications"]:
            medication_display = medication["description"]
            medication_category = categorize_med(medication_display, medication_keywords)
            if medication_category:
                medication["suggestedCategory"] = medication_category
                relevant_medications.append(medication)

    if type(medication_keywords) == set:
        for medication in request_body["evidence"]["medications"]:
            medication_display = medication["description"]
            for med in [x.lower() for x in medication_keywords]:
                if med in medication_display.lower():
                    relevant_medications.append(medication)

    relevant_medications = sorted(
        relevant_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )
    response.update({
        "medications": relevant_medications,
        "relevantMedCount": len(relevant_medications),
        "totalMedCount": len(request_body["evidence"]["medications"])})
    return response
