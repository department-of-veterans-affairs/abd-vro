import csv
import json
import os
from abc import ABC
from typing import Any

from .table_version import TABLE_VERSION
from .dropdown_table_version import DROPDOWN_TABLE_VERSION

# csv file exported from DC Lookup v0.1
# https://docs.google.com/spreadsheets/d/18Mwnn9-cvJIRRupQyQ2zLYOBm3bd0pr4kKlsZtFiyc0/edit#gid=1711756762
dc_table_name = f"Contention Classification Diagnostic Codes Lookup table master sheet - DC Lookup {TABLE_VERSION}.csv"
dropdown_table_name = f"Contention dropdown to classification master - Dropdown Lookup {DROPDOWN_TABLE_VERSION}.csv"
DC_TABLE_FILEPATH = os.path.join(os.path.dirname(__file__), "data", "dc_lookup_table", dc_table_name)
DROPDOWN_TABLE_FILEPATH = os.path.join(os.path.dirname(__file__), "data", "dropdown_lookup_table", dropdown_table_name)

class LookupTable(ABC):
    """ Generalized lookup table for mapping input strings to contention classification codes """
    csv_filepath = None
    def __init__(self):
        if not self.csv_filepath:
            raise NotImplementedError("csv_filepath must be set in child class")
        self.mappings = get_lookup_table(self.csv_filepath)

    def __len__(self):
        return len(self.mappings)

    def get(self, input_str, fallback=None):
        return self.mappings.get(input_str, fallback)

class DropdownLookupTable(LookupTable):
    """ Lookup table for mapping dropdown values to contention classification codes """
    csv_filepath = DROPDOWN_TABLE_FILEPATH

    def __init__(self):
        super().__init__()

class DiagnosticCodeLookupTable(LookupTable):
    """ Lookup table for mapping diagnostic codes to contention classification codes """
    csv_filepath = DC_TABLE_FILEPATH

    def __init__(self):
        super().__init__()

def get_lookup_table(filepath):
    diagnostic_code_to_classification_code = {}
    with open(filepath, "r") as f:
        csv_reader = csv.reader(f)
        for index, csv_line in enumerate(csv_reader):
            if index == 0:
                continue
            diagnostic_code, _, classification_code, _, _, _ = csv_line
            diagnostic_code = int(diagnostic_code)
            try:
                classification_code = int(
                    json.loads(classification_code)[0]
                )  # for v0.1
            except TypeError:
                classification_code = int(json.loads(classification_code))  # for v0.2+
            diagnostic_code_to_classification_code[
                diagnostic_code
            ] = classification_code
    return diagnostic_code_to_classification_code


