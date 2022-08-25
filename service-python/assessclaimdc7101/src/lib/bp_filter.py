from datetime import datetime, date

from dateutil.relativedelta import relativedelta


def bp_recency(request_body):


    valid_bp_readings = []

    if "date_of_claim" in request_body:
        date_of_claim = request_body["date_of_claim"]
    else:
        date_of_claim = str(date.today())


    bp_readings = request_body["evidence"]["bp_readings"]
    date_of_claim_date = datetime.strptime(date_of_claim, "%Y-%m-%d").date()

    for reading in bp_readings:
        bp_reading_date = datetime.strptime(reading["date"], "%Y-%m-%d").date()
        if bp_reading_date >= date_of_claim_date - relativedelta(years=1):
            valid_bp_readings.append(reading)
        
    return valid_bp_readings
