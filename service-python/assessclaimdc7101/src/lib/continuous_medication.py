from datetime import datetime

from dateutil.relativedelta import relativedelta

from .utils import extract_date, format_date


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


def filter_mas_medication(request_body):
    """Filter MAS medication data"""
    response = {}
    medication_with_date = []
    medication_without_date = []
    medication_two_years = []
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])

    for medication in request_body["evidence"]["medications"]:
        if medication["dataSource"] == "MAS":
            try:
                date = datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date()
                medication["dateFormatted"] = format_date(date)
                medication_with_date.append(medication)
                if date >= date_of_claim_date - relativedelta(years=2):
                    medication_two_years.append(medication)
            except (ValueError, KeyError):
                medication["dateFormatted"] = ''
                medication_without_date.append(medication)

    medication_with_date = sorted(
        medication_with_date,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    medication_two_years = sorted(
        medication_two_years,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    medication_with_date.extend(medication_without_date)
    medication_display = medication_with_date

    if request_body["disabilityActionType"] == "INCREASE":
        medication_display = medication_two_years

    response["medications"] = medication_display
    response["medicationsCount"] = len(medication_display)

    return response
