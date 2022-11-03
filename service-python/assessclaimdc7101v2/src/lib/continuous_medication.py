from datetime import datetime

hypertension_medications = {
    "benazepril",
    "lotensin",
    "captopril",
    "capoten",
    "enalapril",
    "enalaprilat",
    "fosinopril",
    "monopril",
    "lisinopril",
    "prinivil",
    "zestril",
}


def continuous_medication_required(request_body):
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
        medication_display = medication["description"]
        flagged = False
        for keyword in hypertension_medications:
            if keyword in medication_display.lower():
                medication["conditionRelated"] = "true"
                relevant_medications.append(medication)
                flagged = True
                break
        if not flagged:
            medication["conditionRelated"] = "false"
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
