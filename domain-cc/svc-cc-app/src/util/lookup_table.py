import json
import os

# lookup table generated from code in vagov-claim-classification-data/create_mdeo_dc_to_cc_mapping.py
LOOKUP_TABLE_PATH = os.path.join(os.path.dirname(__file__), "data", "dc_lookup_table_mvp.json")
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
    with open(LOOKUP_TABLE_PATH, "r") as fh:
        lookup_table = json.load(fh)
        lookup_table = {
            int(diagnostic_code): classification_id
            for diagnostic_code, classification_id in lookup_table.items()
        }
        return lookup_table


def get_classification_name(classification_code):
    try:
        return CLASSIFICATION_NAMES_BY_CODE[classification_code]
    except KeyError:
        return None
