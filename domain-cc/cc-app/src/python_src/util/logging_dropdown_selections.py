import csv
import os

from .table_versions import CONDITION_DROPDOWN_TABLE_VERSION

path = os.path.join(
    os.path.dirname(__file__),
    "data",
    "condition_dropdown_lookup_table",
    f"Contention dropdown to classification master - Dropdown Lookup {CONDITION_DROPDOWN_TABLE_VERSION}.csv",
)


def build_logging_table() -> list:
    """
    Builds list of dropdown options to use for logging from the most current
    dropdown conditions lookup table csv.
    """
    dropdown_values = []
    with open(path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            for k in [
                "UI Term 1",
                "UI Term 2",
                "UI Term 3",
                "UI Term 4",
                "UI Term 5",
                "UI Term 6",
            ]:
                if row[k]:
                    dropdown_values.append(row[k].strip().lower())
        return dropdown_values
