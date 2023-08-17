import csv
import json
import os

from .table_version import TABLE_VERSION

# csv file exported from DC Lookup v0.1
# https://docs.google.com/spreadsheets/d/18Mwnn9-cvJIRRupQyQ2zLYOBm3bd0pr4kKlsZtFiyc0/edit#gid=1711756762
TABLE_NAME = f"Contention Classification Diagnostic Codes Lookup table master sheet - DC Lookup {TABLE_VERSION}.csv"

# sourced from Lighthouse Benefits Reference Data /disabilities endpoint:
# https://developer.va.gov/explore/benefits/docs/benefits_reference_data?version=current
BRD_CLASSIFICATIONS_PATH = os.path.join(
    os.path.dirname(__file__), "data", "lh_brd_classification_ids.json"
)


def get_classification_names_by_code():
    name_by_code = {}
    with open(BRD_CLASSIFICATIONS_PATH, "r") as fh:
        disability_items = json.load(fh)["items"]
        for item in disability_items:
            name_by_code[item["id"]] = item["name"]
    return name_by_code


CLASSIFICATION_NAMES_BY_CODE = get_classification_names_by_code()


def get_lookup_table():
    filename = os.path.join(os.path.dirname(__file__), "data", TABLE_NAME)
    diagnostic_code_to_classification_code = {}
    with open(filename, "r") as fh:
        csv_reader = csv.DictReader(fh)
        for csv_line in csv_reader:
            diagnostic_code = int(csv_line["DIAGNOSTIC_CODE"])
            classification_code = int(csv_line["CLASSIFICATION_CODE"])
            diagnostic_code_to_classification_code[diagnostic_code] = classification_code

    return diagnostic_code_to_classification_code


def get_classification_name(classification_code):
    try:
        return CLASSIFICATION_NAMES_BY_CODE[classification_code]
    except KeyError:
        return None
