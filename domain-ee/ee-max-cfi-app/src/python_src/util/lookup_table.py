import csv
import os

from .table_version import TABLE_VERSION

TABLE_NAME = f"Max Ratings DC Lookup Table {TABLE_VERSION}.csv"


def get_max_ratings_by_code():
    filename = os.path.join(os.path.dirname(__file__), "data", TABLE_NAME)
    diagnostic_code_to_max_rating = {}
    with open(filename, "r") as file:
        csv_reader = csv.reader(file)
        for index, csv_line in enumerate(csv_reader):
            if index == 0:
                continue

            if len(csv_line) != 7:
                raise ValueError(f"Invalid CSV line at index {index}")

            diagnostic_code, rated_issue_name, max_rating, body_system, category, subcategory, cfr_ref = csv_line
            if not diagnostic_code or not max_rating:
                continue

            try:
                diagnostic_code = int(diagnostic_code)
                max_rating = int(max_rating)
            except ValueError:
                raise ValueError(f"Invalid diagnostic code or max rating at index {index}: \n{csv_line}")

            diagnostic_code_to_max_rating[diagnostic_code] = max_rating
    return diagnostic_code_to_max_rating


MAX_RATINGS_BY_CODE = get_max_ratings_by_code()


def get_max_rating(classification_code):
    return MAX_RATINGS_BY_CODE.get(classification_code)
