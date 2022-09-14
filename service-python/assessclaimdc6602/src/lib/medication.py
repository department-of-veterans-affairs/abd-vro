from datetime import datetime
from . import codesets


def categorize_med(medication_display):
    """
    Return the category that a medication belongs to. If it does not belong to any, return an empty list.

    :param medication_display: medication text
    :return: list
    """
    medication_dict = codesets.medication_codesets.med_dict
    flag = []
    for category in list(medication_dict.keys()):
        if flag:
            # most general category has been identified
            break
        for medication in [x.lower() for x in medication_dict[category]]:
            if medication in medication_display.lower():
                flag.append(category)
                break
    return flag


def medication_required(request_body):
    """
    Determine if there is the veteran requires continuous medication for hypertension
    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """
    relevant_medications = []
    other_medications = []

    veterans_medication = request_body["evidence"]["medications"]
    for medication in veterans_medication:
        if medication["status"].lower() == "active":
            medication_display = medication["description"]
            flag = categorize_med(medication_display)
            medication["asthma_relevant"] = flag
            if flag:
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

    relevant_medications.extend(other_medications)

    return relevant_medications
