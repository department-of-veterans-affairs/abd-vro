from datetime import datetime

import pytz
from dateutil import parser
from dateutil.relativedelta import relativedelta


def pdf_helper_0000(data):
    return data


def pdf_helper_all(data):
    # Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp)
    data["start_date"] = datetime.now() - relativedelta(years=1)
    data["timestamp"] = pytz.utc.localize(datetime.now())
    if "veteranInfo" in data:
        data["veteran_info"] = data["veteranInfo"]
    data["evidence"] = data["evidence"]
    if data["veteran_info"]["birthdate"] != "":
        data["veteran_info"]["birthdate"] = parser.parse(data["veteran_info"]["birthdate"])
    if "evidence" in data:
        for medication_info in data["evidence"]["medications"]:
            medication_info["authoredOn"] = parser.parse(medication_info["authoredOn"])
    return data


def toc_helper_all(toc_file_path, data):  # pragma: no cover
    file_data = None

    with open(toc_file_path, 'r') as file:
        file_data = file.read()

    file_data = file_data.replace("{{name}}", f"{data['veteran_info']['first']} {data['veteran_info']['middle']} {data['veteran_info']['last']}")
    file_data = file_data.replace("{{file}}", data['fileIdentifier'])
    file_data = file_data.replace("{{date}}", f"{pytz.utc.localize(datetime.now()).strftime('%b. %d, %Y')}")

    generated_toc_path = toc_file_path.replace("base_toc", "generated_toc")

    with open(generated_toc_path, 'w') as file:
        file.write(file_data)
    return generated_toc_path
