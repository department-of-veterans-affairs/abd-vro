import os
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


def pdf_helper_all(data: dict) -> dict:
    """Adds/modifies the data for all PDFs like adding the timestamp
    :param data: PDF data that will be used in the template
    :type data: dict
    :return: The modified PDF data
    :rtype: dict
    """

    data["timestamp"] = pytz.utc.localize(datetime.now())
    # Find out if the PDF is being generated locally(VENV, script, etc.) or through Docker. Depending on which, set the appropriate base path for fonts, images, etc.
    run_mode = os.environ.get("DOCKER", "local")
    if run_mode == "docker":
        data["base_path"] = "/app/public"
    else:
        current_dir = os.path.abspath(os.path.dirname(__file__))
        data["base_path"] = os.path.join(current_dir, "..", "public")
    # Starting date from when the data is fetched. Mainly to be used to display a range Ex: (start_date) to (timestamp)
    data["start_date"] = datetime.now() - relativedelta(years=1)
    data["evidence"] = data["evidence"]
    if data["veteranInfo"]["birthdate"] != "":
        birth_date = data["veteranInfo"]["birthdate"].replace("Z", "")
        data["veteranInfo"]["birthdate"] = parser.parse(birth_date)

    if data["version"] == "v1":
        for medication_info in data["evidence"]["medications"]:
            medication_info["authoredOn"] = parser.parse(medication_info["authoredOn"])

    return data


def toc_helper_all(toc_file_path, data) -> str:  # pragma: no cover
    """Modifies the ToC before it is attached to the PDF

    :param toc_file_path: File path for the base_toc.xsl
    :type toc_file_path: str
    :param data: PDF data that will be used in the template
    :type data: dict
    :return: The file path for the modified ToC
    :rtype: str
    """
    file_data = None

    with open(toc_file_path, 'r') as file:
        file_data = file.read()

    # Fill in the base path for fonts, images, etc.
    file_data = file_data.replace("{{base_path}}", data['base_path'])
    # Fill in the veteran's info like name, dob, identifier, etc.
    file_data = file_data.replace("{{name}}", f"{data['veteranInfo']['first']} {data['veteranInfo']['middle']} {data['veteranInfo']['last']}")
    file_data = file_data.replace("{{file}}", data['veteranFileId'])
    # Fill in the date the ToC/PDF was generated
    file_data = file_data.replace("{{date}}", f"{pytz.utc.localize(datetime.now()).strftime('%b. %d, %Y')}")

    generated_toc_path = toc_file_path.replace("base_toc", f"{data['claimSubmissionId']}_toc")

    with open(generated_toc_path, 'w') as file:
        file.write(file_data)
    return generated_toc_path
