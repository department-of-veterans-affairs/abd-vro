
from datetime import datetime

from dateutil.relativedelta import relativedelta

from .utils import extract_date, format_date


def sort_bp(bp_readings):
    """
    Sort bp readings by date
    :param bp_readings: List of bp readings
    :return:
    """

    bp_readings = sorted(
        bp_readings,
        key=lambda i: datetime.strptime(i["date"], "%Y-%m-%d").date(),
        reverse=True,
    )
    return bp_readings


def bp_reader(request_body):
    """
    Determine if there is enough BP data to calculate a predominant reading,
    and if so return the predominant rating

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    bp_reading_in_past_year = []
    bp_readings_in_past_two_years = []
    elevated_bp = []
    date_of_claim_date = extract_date(request_body["claimSubmissionDateTime"])
    bp_readings = request_body["evidence"]["bp_readings"]
    bp_readings = sort_bp(bp_readings)

    for reading in bp_readings:
        bp_reading_date = datetime.strptime(reading["date"], "%Y-%m-%d").date()
        reading["dateFormatted"] = format_date(bp_reading_date)
        if bp_reading_date >= date_of_claim_date - relativedelta(years=1):
            bp_reading_in_past_year.append(reading)
        if bp_reading_date >= date_of_claim_date - relativedelta(years=2):
            bp_readings_in_past_two_years.append(reading)
            if reading["systolic"]["value"] >= 160 and reading["diastolic"]["value"] >= 100:
                elevated_bp.append(reading)

    predominance_calculation = {"twoYearsBp": bp_readings_in_past_two_years,
                                "oneYearBp": bp_reading_in_past_year,
                                "twoYearsBpReadings": len(bp_readings_in_past_two_years),
                                "oneYearBpReadings": len(bp_reading_in_past_year),
                                "recentElevatedBpReadings": len(elevated_bp),
                                "totalBpReadings": len(request_body["evidence"]["bp_readings"])}

    return predominance_calculation
