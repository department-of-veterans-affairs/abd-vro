from datetime import datetime

from . import codesets


def categorize_med(medication_display):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.

    :param medication_display: medication text
    :return: list
    """
    medication_dict = codesets.medication_codesets.med_dict
    medication_category = []
    for category_id in list(medication_dict.keys()):
        if medication_category:
            # most general category has been identified
            break
        for medication in medication_dict[category_id]:
            if medication in medication_display.lower():
                medication_category.append(category_id)
                break
    return medication_category


def identify_relevant_med(medication_display):
    relevant = "false"
    for keyword in codesets.medication_codesets.asthma_medications:
        if keyword in medication_display.lower():
            relevant = "true"
            break
    return relevant


def medication_required(request_body, include_category=False):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param include_category:
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    response = {}
    relevant_medications = []
    other_medications = []

    veterans_medication = request_body["evidence"]["medications"]
    for medication in veterans_medication:
        if medication["status"].lower() == "active":
            medication_display = medication["description"]
            medication["asthmaRelevant"] = identify_relevant_med(medication_display)
            if include_category:
                category = categorize_med(medication_display)
                medication["suggestedCategory"] = category
            if medication["asthmaRelevant"]:
                relevant_medications.append(medication)
            elif not medication["asthmaRelevant"]:
                other_medications.append(medication)

    relevant_medications = sorted(
        relevant_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    other_medications = sorted(
        other_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    response["relevantMedCount"] = len(relevant_medications)
    relevant_medications.extend(other_medications)
    response["totalMedCount"] = len(relevant_medications)
    response["medications"] = relevant_medications

    return response
