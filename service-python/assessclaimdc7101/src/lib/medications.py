from datetime import datetime

from dateutil.relativedelta import relativedelta

from utils import extract_date, format_date


def sort_med(medication_list):
    """
    Sort medications by 'authoredOn' date.

    :param medication_list: List of medication
    :return: Sorted list
    """

    medication_list = sorted(
        medication_list,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )

    return medication_list


def medication_required(request_body):
    """
    Determine if there is the veteran requires medication for hypertension
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

    response["medications"] = sort_med(relevant_medications)
    response["medicationsCount"] = len(relevant_medications)
    return response


def filter_mas_medication(request_body):
    """Filter MAS medication data"""
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
            try:
                medication["receiptDate"] = format_date(datetime.strptime(medication["receiptDate"], "%Y-%m-%d").date())
            except (ValueError, KeyError):
                medication["receiptDate"] = ""

    response = {"twoYearsMedications": sort_med(medication_two_years),
                "allMedications": sort_med(medication_with_date) + medication_without_date,
                "allMedicationsCount": len(request_body["evidence"]["medications"]),
                "twoYearsMedicationsCount": len(medication_two_years)
                }

    return response
