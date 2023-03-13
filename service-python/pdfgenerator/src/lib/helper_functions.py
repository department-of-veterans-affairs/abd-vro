from datetime import datetime

import pytz
from dateutil import parser
from dateutil.relativedelta import relativedelta


def set_empty_readings(value):
    if value == 0:
        string_value = "-"
    else:
        string_value = str(int(value))
    return string_value


def pdf_helper_hypertension(data):
    for reading in data["evidence"]["bp_readings"]:
        reading["systolic"]["value"] = set_empty_readings(reading["systolic"]["value"])
        reading["diastolic"]["value"] = set_empty_readings(reading["diastolic"]["value"])
    return data


def pdf_helper_all(data):
    # Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp)
    data["start_date"] = datetime.now() - relativedelta(years=1)
    data["timestamp"] = pytz.utc.localize(datetime.now())
    if "evidence" in data:
        data["evidence"] = data["evidence"]
    if "veteranInfo" in data and data["veteranInfo"]["birthdate"] != "":
        birth_date = data["veteranInfo"]["birthdate"].replace("Z", "")
        data["veteranInfo"]["birthdate"] = parser.parse(birth_date)

    if "version" in data and data["version"] == "v1":
        for medication_info in data["evidence"]["medications"]:
            medication_info["authoredOn"] = parser.parse(medication_info["authoredOn"])

    return data


def toc_helper_all(toc_file_path, data):  # pragma: no cover
    file_data = None

    with open(toc_file_path, 'r') as file:
        file_data = file.read()

    file_data = file_data.replace("{{name}}",
                                  f"{data['veteranInfo']['first']} {data['veteranInfo']['middle']} {data['veteranInfo']['last']}")
    file_data = file_data.replace("{{file}}", data['veteranFileId'])
    file_data = file_data.replace("{{date}}", f"{pytz.utc.localize(datetime.now()).strftime('%b. %d, %Y')}")

    generated_toc_path = toc_file_path.replace("base_toc", f"{data['claimSubmissionId']}_toc")

    with open(generated_toc_path, 'w') as file:
        file.write(file_data)
    return generated_toc_path
