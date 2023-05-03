from datetime import datetime

from .codesets import rhinitis_medication


def categorize_med(medication_display):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.
    :param medication_display: medication text
    :return: list
    """
    medication_dict = rhinitis_medication.rhinitis_med
    medication_category = str()
    for category_id in list(medication_dict.keys()):
        if medication_category:
            break
        for medication in medication_dict[category_id]:
            if medication.lower() in medication_display.lower():
                medication_category = category_id
                break
    return medication_category


def medication_required(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
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
        if medication["status"].lower() in ["active", "on-hold", "completed", "stopped", "unknown"]:
            medication["conditionRelated"] = False
            medication_display = medication["description"]
            category = categorize_med(medication_display)
            if category:
                medication["suggestedCategory"] = category
                medication["conditionRelated"] = True
                relevant_medications.append(medication)
            else:
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
