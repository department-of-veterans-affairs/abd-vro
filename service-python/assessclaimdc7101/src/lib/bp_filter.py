from datetime import date, datetime

from dateutil.relativedelta import relativedelta


def bp_recency(request_body):

    response = {}
    valid_bp_readings = []

    if "dateOfClaim" in request_body:
        date_of_claim = request_body["dateOfClaim"]
    else:
        date_of_claim = str(date.today())

    bp_readings = request_body["evidence"]["bp_readings"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    for reading in bp_readings:
        bp_reading_date = datetime.strptime(reading["date"], "%Y-%m-%d").date()
        if bp_reading_date >= date_of_claim_date - relativedelta(years=1):
            valid_bp_readings.append(reading)

    response["totalBpReadings"] = len(bp_readings)
    response["recentBpReadings"] = len(valid_bp_readings)
    response["bpReadings"] = valid_bp_readings
    return response
