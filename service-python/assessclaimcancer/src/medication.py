from datetime import datetime
from codesets import head_cancer_medication, male_reproductive_medication, gyn_cancer_medication, \
    neck_medication, prostate_cancer_medication, melanoma_medication, pancreatic_medication

medication_codeset_map = {"head": head_cancer_medication.medication_keywords,
                          "neck": neck_medication.medications,
                          "male_reproductive": male_reproductive_medication.medications,
                          "gyn": gyn_cancer_medication.medications,
                          "prostate": prostate_cancer_medication.medication_keywords,
                          "melanoma": melanoma_medication.medications,
                          "pancreatic": pancreatic_medication.medication_keywords}


# not one to one with VASRD

# Head, Neck, Respiratory, GI, Reproductive, Kidney, Brain, Melanoma, Pancreatic, Prostate Cancer
def medication_match(request_body):
    """
    Determine if there is the veteran requires continuous medication for cancer

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    result = {}
    date_of_claim = request_body["dateOfClaim"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()
    cancer_type = "head"

    medication_codes = medication_codeset_map[cancer_type]
    if type(medication_codes) == dict:
        for medication in request_body["medication"]:
            if medication["text"].lower() in [x.lower() for x in medication_codes]:

    if type(medication_codes) == list:
        for medication in request_body["medication"]:
            if medication["text"].lower() in [x.lower() for x in medication_codes]:

    return result
