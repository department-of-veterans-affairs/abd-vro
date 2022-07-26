import operator
from datetime import datetime

from dateutil.relativedelta import relativedelta


def bp_readings_meet_date_specs(date_of_claim, bp_readings):
    """
    Determine from a list of BP readings if there exists two readings with a 1 month and 6 month date window 

    :param date_of_claim: string of date of claim
    :type date_of_claim: string
    :param bp_readings: list of blood pressure readings
    :type bp_readings: list
    :return: boolean indicating if readings within the two date windows are present
    :rtype: boolean
    """

    reading_within_one_month = False
    reading_within_six_months = False
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    for reading in bp_readings:
        bp_reading_date = datetime.strptime(reading["date"], "%Y-%m-%d").date()
        one_month_difference = (date_of_claim_date - bp_reading_date).days <= 30
        six_month_difference = (date_of_claim_date - bp_reading_date).days <= 180

        if (one_month_difference and not reading_within_one_month):
            reading_within_one_month = True
        elif (one_month_difference and reading_within_one_month):
            reading_within_six_months = True
        elif (six_month_difference and not reading_within_six_months):
            reading_within_six_months = True

    return reading_within_one_month and reading_within_six_months


def calculate_predominant_readings(bp_readings):
    """
    Calculate the predominant diastolic and systolic values from a list of BP readings

    :param bp_readings: list of blood pressure readings
    :type bp_readings: list
    :return: dictionary with systolic and diastolic values 
    :rtype: dict
    """

    if len(bp_readings) == 2:
        return {
            "systolic_value": max(bp_readings[0]["systolic"]["value"], bp_readings[1]["systolic"]["value"]),
            "diastolic_value": max(bp_readings[0]["diastolic"]["value"], bp_readings[1]["diastolic"]["value"])
        }

    diastolic_130_and_above = []
    diastolic_120_to_129 = []
    diastolic_110_to_119 = []
    diastolic_100_to_109 = []
    diastolic_0_to_99 = []

    systolic_200_and_above = []
    systolic_160_to_199 = []
    systolic_0_to_159 = []

    for reading in bp_readings:
        diastolic_value = reading["diastolic"]["value"]
        systolic_value = reading["systolic"]["value"]

        if diastolic_value >= 130:
            diastolic_130_and_above.append(reading)
        elif diastolic_value >= 120 and diastolic_value < 130:
            diastolic_120_to_129.append(reading)
        elif diastolic_value >= 110 and diastolic_value < 120:
            diastolic_110_to_119.append(reading)
        elif diastolic_value >= 100 and diastolic_value < 110:
            diastolic_100_to_109.append(reading)
        else:
            diastolic_0_to_99.append(reading)

        if systolic_value >= 200:
            systolic_200_and_above.append(reading)
        elif systolic_value >= 160:
            systolic_160_to_199.append(reading)
        else:
            systolic_0_to_159.append(reading)

    # This is a **list** of lists (rather than a dict of lists) because we want
    # to preserve order...
    # ...and we want to preserve order because, when there is a tie in the
    # total number of readings in mulitiple BP range buckets, we want to pick
    # the BP range bucket of the higher BP range.
    list_of_diastolic_lists = [
        diastolic_120_to_129,
        diastolic_110_to_119,
        diastolic_100_to_109,
        diastolic_0_to_99
    ]

    list_of_systolic_lists =[
        systolic_160_to_199,
        systolic_0_to_159
    ]

    longest_diastolic_list = diastolic_130_and_above
    longest_systolic_list = systolic_200_and_above

    for diastolic_list in list_of_diastolic_lists:
        if len(diastolic_list) > len(longest_diastolic_list):
            longest_diastolic_list = diastolic_list

    for systolic_list in list_of_systolic_lists:
        if len(systolic_list) > len(longest_systolic_list):
            longest_systolic_list = systolic_list

    longest_diastolic_list.sort(key=operator.itemgetter("effectiveDateTime"))

    longest_systolic_list.sort(key=operator.itemgetter("effectiveDateTime"))

    return {
        "systolic_value": longest_systolic_list[-1]["systolic"]["value"],
        "diastolic_value": longest_diastolic_list[-1]["diastolic"]["value"]
    }

def sufficient_to_autopopulate (request_body):
    """
    Determine if there is enough BP data to calculate a predominant reading,
    and if so return the predominant rating

    :param request_body: request body
    :type request_body: dict
    :return: response body indicating success or failure with additional attributes
    :rtype: dict
    """

    predominance_calculation = {}
    date_of_claim = request_body["date_of_claim"]
    valid_bp_readings = []
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    for reading in request_body["observation"]["bp_readings"]:
        bp_reading_date = datetime.strptime(reading["effectiveDateTime"], "%Y-%m-%d").date()
        if bp_reading_date >= date_of_claim_date - relativedelta(years=1):
            valid_bp_readings.append(reading)

    if len(valid_bp_readings) <= 1 or not bp_readings_meet_date_specs(date_of_claim, valid_bp_readings):
        predominance_calculation["success"] = False
        return predominance_calculation

    elif len(valid_bp_readings) > 1 and bp_readings_meet_date_specs(date_of_claim, valid_bp_readings):
        results = calculate_predominant_readings(valid_bp_readings)
        predominance_calculation["success"] = True
        predominance_calculation["predominant_diastolic_reading"] = results["diastolic_value"]
        predominance_calculation["predominant_systolic_reading"] = results["systolic_value"]

    return predominance_calculation
