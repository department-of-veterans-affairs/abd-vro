from datetime import datetime


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

    veterans_medication = request_body["evidence"]["medications"]
    for medication in veterans_medication:
        if medication["status"].lower() == "active":
            relevant_medications.append(medication)

    relevant_medications = sorted(
        relevant_medications,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )
    response["medications"] = relevant_medications
    response["medicationsCount"] = len(relevant_medications)
    return response


def filter_mas_medication(event):
    """Filter MAS medication data"""
    response = {}
    medication_with_date = []
    medication_without_date = []

    for medication in event["evidence"]["medications"]:
        if "authoredOn" in medication.keys():
            if medication["authoredOn"]:
                date = datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date()
                medication["dateFormatted"] = date.strftime("%m/%d/%Y")
                medication_with_date.append(medication)
            else:
                medication_without_date.append(medication)
        else:
            medication_without_date.append(medication)

    medication_with_date = sorted(
        medication_with_date,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    medication_with_date.extend(medication_without_date)
    response["medications"] = medication_with_date
    response["medicationsCount"] = len(medication_with_date)

    return response
