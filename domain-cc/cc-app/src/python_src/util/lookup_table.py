import csv
import os

from .table_versions import (
    CONDITION_DROPDOWN_TABLE_VERSION,
    DIAGNOSTIC_CODE_TABLE_VERSION,
)

# https://docs.google.com/spreadsheets/d/18Mwnn9-cvJIRRupQyQ2zLYOBm3bd0pr4kKlsZtFiyc0/edit#gid=1711756762
dc_table_name = (
    f"[Release notes] Diagnostic Code to Classification mapping release notes - "
    f"DC Lookup {DIAGNOSTIC_CODE_TABLE_VERSION}.csv"
)
# https://docs.google.com/spreadsheets/d/1A5JuYwn39mHE5Mk1HazN-mxCL2TENPeyUPHHhH10g_I/edit#gid=819850041
previous_condition_dropdown_table_name = (
    "Contention dropdown to classification master - Dropdown Lookup v0.1.csv"
)

contention_lut_csv_filename = (
    f"[Release notes] Contention Text to Classification mapping release notes - Contention Text Lookup "
    f"{CONDITION_DROPDOWN_TABLE_VERSION}.csv"
)

LUT_DEFAULT_VALUE = {
    "classification_code": None,
    "classification_name": None,
}


class DiagnosticCodeLookupTable:
    """
    Lookup table for mapping diagnostic codes to contention classification codes
    """

    CSV_FILEPATH = os.path.join(
        os.path.dirname(__file__), "data", "dc_lookup_table", dc_table_name
    )
    input_key = "DIAGNOSTIC_CODE"
    output_key = "CLASSIFICATION_CODE"

    def __init__(self):
        self.classification_code_mappings = {}
        with open(self.CSV_FILEPATH, "r") as fh:
            csv_reader = csv.DictReader(fh)
            for row in csv_reader:
                table_key = row[str(self.input_key)].strip().lower()
                self.classification_code_mappings[table_key] = {
                    "classification_code": int(row[self.output_key]),
                    "classification_name": row[
                        "CLASSIFICATION_TEXT"
                    ],  # note underscore different from contention LUT
                }

    def get(self, input_key: int, default_value=LUT_DEFAULT_VALUE):
        classification = self.classification_code_mappings.get(
            str(input_key), default_value
        )
        return classification

    def __len__(self):
        return len(self.classification_code_mappings)


def get_v1_lookup_table(filepath: str, input_key: str, output_key: str) -> dict:
    """
    Returns the lookup table for the diagnostic code and original condition
    dropdown list

    Parameters
    ----------
    filepath: os.path
        Path to the csv files containing the data for the version 1 LUTs
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
                    text_to_convert = csv_line[input_key].strip().lower()
                classification_code = int(csv_line[output_key])
                classification_code_mappings[text_to_convert] = classification_code
            except KeyError:
                print(f"csv_line: {csv_line}")
                raise

    return classification_code_mappings


class ContentionTextLookupTable:
    """
    Maps contention text to classification code
    For mapping autosuggestion combobox values of the add disabilities page of the 526-ez form
      as well as free text entries to a classification (name and code)
    """

    CSV_FILEPATH = os.path.join(
        os.path.dirname(__file__),
        "data",
        "condition_dropdown_lookup_table",
        contention_lut_csv_filename,
    )
    input_key = "CONTENTION TEXT"
    output_key = "CLASSIFICATION CODE"
    classification_code_mappings = {}

    def __init__(self):
        with open(self.CSV_FILEPATH, "r") as fh:
            csv_reader = csv.DictReader(fh)
            for row in csv_reader:
                table_key = row[self.input_key].strip().lower()
                self.classification_code_mappings[table_key] = {
                    "classification_code": int(row[self.output_key]),
                    "classification_name": row["CLASSIFICATION TEXT"],
                }

    def get(self, input_str: str, default_value=LUT_DEFAULT_VALUE):
        input_str = input_str.strip().lower()
        classification = self.classification_code_mappings.get(input_str, default_value)
        return classification

    def __len__(self):
        return len(self.classification_code_mappings)


def get_lookup_table(
    version_num: str,
    v2_filepath: str,
    v1_mapping_filepath: str,
    input_key: str,
    output_key: str,
    v2_input_key: list,
    v2_output_key: str,
) -> dict:
    """
    Build the full lookup table with version LUT and condition dropdown LUT

    Parameters
    -----------
    version_num: str
        Version number of the condition dropdown table
    dropdown_v2_filepath: str
        Filepath to the new condition dropdown table
    v1_mapping_filepath: str
        Filepath to original csv files for both diagnostic code and
        condition dropdown
    input_key: str
        CSV column name used to build the v1 LUTs either
        DIAGNOSTIC_CODE or CONTENTION_TEXT
    output_key: str
        CSV column name used to build the v1 LUTs equal to CLASSIFICATION_CODE
    v2_input_key: list
        CSV column names used to build the v2 condition lookup table
    v2_output_key: str
        CSV column name used to build the v2 condition lookup table equal to
        'Classification Code'

    Returns
    --------
    dict
        keys: either the diagnostic code of condition dropdown value
        values: classification codes
    """
    classification_code_mappings = get_v1_lookup_table(
        v1_mapping_filepath, input_key, output_key
    )
    if float(version_num.split("v")[1]) >= 0.1:
        # add new dropdown values to LUT
        try:
            with open(v2_filepath, "r") as fh:
                csv_reader = csv.DictReader(fh)
                for row in csv_reader:
                    for k in v2_input_key:
                        if row[k] and row[v2_output_key]:
                            classification_code_mappings[row[k].strip().lower()] = int(
                                row[v2_output_key]
                            )
        # raises exception if dropdown_v2_filepath is None (only create DC LUT)
        except TypeError:
            return classification_code_mappings
    return classification_code_mappings
