from datetime import datetime

from dateutil.relativedelta import relativedelta

from .utils import extract_date, format_date


def sort_bp(bp_readings):
    """
    Sort bp readings by date.

    :param bp_readings: List of bp readings
    :return: Sorted list
    """

    bp_readings = sorted(
        bp_readings,
        key=lambda i: datetime.strptime(i["date"], "%Y-%m-%d").date(),
        reverse=True,
    )
    return bp_readings


def deduplicate(bp_readings):
    """
    Return bp readings with any lighthouse duplicates removed. A duplicate is identified by having the same diastolic
    value, systolic value and date. HDR data has organization set to VAMC Other Output Reports.
    :param bp_readings: full list of BP readings
    :return: deduplicated list
    """
    deduplicated_readings = []
    for reading in bp_readings:
        duplicate = False
        for bp_comp in bp_readings:
            if reading["dataSource"] == "LH" and reading["diastolic"]["value"] == bp_comp["diastolic"]["value"] \
                and reading["systolic"]["value"] == bp_comp["systolic"]["value"] \
                    and reading["date"] == bp_comp["date"] \
                    and bp_comp["organization"] == "VAMC Other Output Reports":
                duplicate = True
                break
        if not duplicate:
            deduplicated_readings.append(reading)
    return deduplicated_readings


def bp_reader(request_body):
    """
    Iterate through all the BP readings received by data sources and determine their recency relative to the date
     of claim. Flag high BP readings.

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    bp_reading_in_past_year = []
    bp_readings_in_past_two_years = []
    elevated_bp_in_past_two_years = []
    sortable_bp = []
    not_sortable_bp = []
    partial_bp_two_years = []
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])
    bp_readings = request_body["evidence"]["bp_readings"]

    deduplicated_bp_readings = deduplicate(bp_readings)
    for reading in deduplicated_bp_readings:
        try:
            reading["receiptDate"] = format_date(datetime.strptime(reading["receiptDate"], "%Y-%m-%d").date())
        except (ValueError, KeyError):
            reading["receiptDate"] = ""
        try:
            bp_reading_date = datetime.strptime(reading["date"], "%Y-%m-%d").date()
            reading["dateFormatted"] = format_date(bp_reading_date)
            sortable_bp.append(reading)
        except ValueError:
            reading["dateFormatted"] = ''
            not_sortable_bp.append(reading)
            continue  # If there is no date associated

        if reading["systolic"]["value"] == 0 or reading["diastolic"]["value"] == 0:
            if bp_reading_date >= date_of_claim_date - relativedelta(years=2):
                partial_bp_two_years.append(reading)  # to be displayed in PDF
            continue

        if bp_reading_date >= date_of_claim_date - relativedelta(years=1):
            bp_reading_in_past_year.append(reading)
        if bp_reading_date >= date_of_claim_date - relativedelta(years=2):
            bp_readings_in_past_two_years.append(reading)
            if reading["systolic"]["value"] >= 160 and reading["diastolic"]["value"] >= 100:
                elevated_bp_in_past_two_years.append(reading)

    result = {"twoYearsBp": sort_bp(bp_readings_in_past_two_years + partial_bp_two_years),
              "oneYearBp": sort_bp(bp_reading_in_past_year),
              "allBp": sort_bp(sortable_bp) + not_sortable_bp,
              "twoYearsBpCount": len(bp_readings_in_past_two_years),
              "oneYearBpCount": len(bp_reading_in_past_year),
              "twoYearsElevatedBpCount": len(elevated_bp_in_past_two_years),
              "totalBpCount": len(deduplicated_bp_readings)}

    return result
