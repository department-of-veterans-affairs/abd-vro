import csv
import os

from .table_versions import CONDITION_DROPDOWN_COVERAGE_VERSION

path = os.path.join(
    os.path.dirname(__file__),
    "data",
    "condition_dropdown_coverage",
    f"[Release notes] Auto-suggestions release notes - 202405 PACT Act - {CONDITION_DROPDOWN_COVERAGE_VERSION}.csv",
)


def build_logging_table() -> list:
    """
    Builds list of dropdown options to use for logging from the most current
    dropdown conditions lookup table csv.
    """
    dropdown_values = []
    with open(path, "r") as f:
        next(f)  # skip "Conditions list terms, organized by base term and variations"
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
