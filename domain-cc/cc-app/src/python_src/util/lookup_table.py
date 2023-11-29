import csv
import os
from abc import ABC

from .table_versions import (
    CONDITION_DROPDOWN_TABLE_VERSION,
    DIAGNOSITC_CODE_TABLE_VERSION,
    REDESIGNED_CONDITIONS_TABLE_VERSION,
)

# https://docs.google.com/spreadsheets/d/18Mwnn9-cvJIRRupQyQ2zLYOBm3bd0pr4kKlsZtFiyc0/edit#gid=1711756762
dc_table_name = (
    f"Contention Classification Diagnostic Codes Lookup table master"
    f" sheet - DC Lookup {DIAGNOSITC_CODE_TABLE_VERSION}.csv"
)
# https://docs.google.com/spreadsheets/d/1A5JuYwn39mHE5Mk1HazN-mxCL2TENPeyUPHHhH10g_I/edit#gid=819850041
condition_dropdown_table_name = (
    f"Contention dropdown to classification master"
    f" - Dropdown Lookup {CONDITION_DROPDOWN_TABLE_VERSION}.csv"
)

redesigned_dropdown_table_name = (
    "Contention dropdown to classification master"
    f" - Prototype - Terms + mapping {REDESIGNED_CONDITIONS_TABLE_VERSION}.csv"
)


class LookupTable(ABC):
    """
    Generalized lookup table for mapping input strings to contention
    classification codes
    """

    CSV_FILEPATH = None
    REDESIGNED_FILEPATH = None
    input_key = None
    output_key = None

    def __init__(self):
        if not self.CSV_FILEPATH:
            raise NotImplementedError("csv_filepath must be set in child class")
        self.mappings = get_lookup_table(
            self.CSV_FILEPATH, input_key=self.input_key, output_key=self.output_key
        )

    def __len__(self):
        return len(self.mappings)

    def get(self, input_str, fallback=None):
        return self.mappings.get(input_str, fallback)


class ConditionDropdownLookupTable(LookupTable):
    """Lookup table for mapping condition dropdown values to contention classification codes"""

    CSV_FILEPATH = os.path.join(
        os.path.dirname(__file__),
        "data",
        "condition_dropdown_lookup_table",
        condition_dropdown_table_name,
    )
    REDESIGNED_FILEPATH = os.path.join(
        os.path.dirname(__file__),
        "data",
        "redesigned_lookup_table",
        redesigned_dropdown_table_name,
    )
    input_key = "CONTENTION_TEXT"
    output_key = "CLASSIFICATION_CODE"

    def __init__(self):
        super().__init__()

    def merge_dictionaries(self) -> dict:
        """
        Function merges the self.mappings dictionary with the dictionary
        containing the mappings for the redesigned list

        Returns
        -------
        dict
            keys: string representing the dropdown option
            values: classification codes
        """
        redesigned_lut = get_redesigned_lookup_table(self.REDESIGNED_FILEPATH)
        return {**self.mappings, **redesigned_lut}

    def get(self, input_str: str, fallback=None):
        """
        Provides the classification code for a given dropdown option

        Parameters
        ----------
        input_str: str
            dropdown option selected by user
        fallback: None
            Default value to return if dropdown option is not in LUT

        Returns
        --------
        int | None
            Returns classification code if in LUT or None
        """
        new_mappings = self.merge_dictionaries()
        input_str = input_str.strip().lower()
        return new_mappings.get(input_str, fallback)


class DiagnosticCodeLookupTable(LookupTable):
    """
    Lookup table for mapping diagnostic codes to contention classification codes
    """

    CSV_FILEPATH = os.path.join(
        os.path.dirname(__file__), "data", "dc_lookup_table", dc_table_name
    )
    input_key = "DIAGNOSTIC_CODE"
    output_key = "CLASSIFICATION_CODE"

    def __init__(self):
        super().__init__()


def get_lookup_table(filepath: os.path, input_key: str, output_key: str) -> dict:
    """
    Returns the lookup table for the diagnostic code and original condition dropdown list

    Parameters
    ----------
    filepath: os.path
        Path to the csv files containing the data for the LUT
    input_key: str
        Key for the dictionary
    output_key: str
        Value for the dictionary

    Returns
    --------
    dict
        keys: either the diagnostic code of condition dropdown value
        values: classification codes
    """
    classification_code_mappings = {}
    with open(filepath, "r") as fh:
        csv_reader = csv.DictReader(fh)
        for csv_line in csv_reader:
            try:
                try:
                    text_to_convert = int(csv_line[input_key])
                except ValueError:
                    text_to_convert = csv_line[input_key].lower()
                classification_code = int(csv_line[output_key])
                classification_code_mappings[text_to_convert] = classification_code
            except KeyError:
                print(f"csv_line: {csv_line}")
                raise

    return classification_code_mappings


def get_redesigned_lookup_table(path: os.path) -> dict:
    """
    Reads in the redesigned list and converts to dict

    Parameters:
    path: os.path

    Returns:
        redesigned_lookup_table: dict
            keys: dropdown condition
            values: classification code

    """
    redesigned_lookup_table = {}
    with open(path, "r") as fh:
        next(fh)
        csv_reader = csv.DictReader(fh)
        for row in csv_reader:
            for k in ["UI Term 1", "UI Term 2", "UI Term 3"]:
                if row[k] and row["Classification Code"]:
                    redesigned_lookup_table[row[k].lower()] = int(
                        row["Classification Code"]
                    )
    return redesigned_lookup_table


test = ConditionDropdownLookupTable()
print(test.get("tinnitus (ringing in the ears)"))
