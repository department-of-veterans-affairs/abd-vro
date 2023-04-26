from datetime import datetime

from dateutil.relativedelta import relativedelta

from utils import extract_date, format_date

from .codesets import medication_codesets


def sort_med(med_list):

    med_list = sorted(
        med_list,
        key=lambda i: datetime.strptime(i["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date(),
        reverse=True,
    )
    return med_list


def classify_med(medication_display):
    """
    Return the class that a medication belongs to. If it does not belong to any, return an empty list.

    :param medication_display: medication text
    :return: list
    """
    medication_dict = medication_codesets.med_dict
    medication_category = str()
    for category_id in list(medication_dict.keys()):
        if medication_category:
            break
        for medication in medication_dict[category_id]:
            if medication in medication_display.lower():
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
        if medication["status"].lower() == "active":
            flagged = False
            medication_display = medication["description"]
            for keyword in [x.lower() for x in medication_codesets.asthma_medications]:
                if keyword in medication_display.lower():
                    medication["asthmaRelevant"] = "true"
                    relevant_medications.append(medication)
                    flagged = True
                    break
            if not flagged:
                medication["asthmaRelevant"] = "false"
                other_medications.append(medication)

    relevant_medications = sort_med(relevant_medications)

    other_medications = sort_med(other_medications)

    response["relevantMedCount"] = len(relevant_medications)
    relevant_medications.extend(other_medications)
    response["totalMedCount"] = len(relevant_medications)
    response["medications"] = relevant_medications

    return response


def filter_categorize_mas_medication(request_body):
    """Filter MAS medication data"""
    medication_with_date = []
    medication_without_date = []
    medication_one_year = []
    relevant_med = 0
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])

    for medication in request_body["evidence"]["medications"]:
        drug_class = classify_med(medication["description"])
        if drug_class:
            relevant_med += 1
        medication["classification"] = drug_class
        try:
            date = datetime.strptime(medication["authoredOn"], "%Y-%m-%dT%H:%M:%SZ").date()
            medication["dateFormatted"] = format_date(date)
            medication_with_date.append(medication)
            if date >= date_of_claim_date - relativedelta(years=1):
                medication_one_year.append(medication)
        except (ValueError, KeyError):
            medication["dateFormatted"] = ''
            medication_without_date.append(medication)

    response = {"oneYearMedication": sort_med(medication_one_year),
                "allMedications": sort_med(medication_with_date) + medication_without_date,
                "allMedicationsCount": len(request_body["evidence"]["medications"]),
                "relevantMedicationCount": relevant_med,
                "oneYearMedicationsCount": len(medication_one_year)
                }

    return response
