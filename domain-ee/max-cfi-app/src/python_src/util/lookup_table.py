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
            diagnostic_code, rated_issue_name, max_rating = csv_line
            diagnostic_code = int(diagnostic_code)
            max_rating = float(max_rating)

            diagnostic_code_to_max_rating[diagnostic_code] = max_rating
    return diagnostic_code_to_max_rating


MAX_RATINGS_BY_CODE = get_max_ratings_by_code()


def get_max_rating(classification_code):
    try:
        return MAX_RATINGS_BY_CODE[classification_code]
    except KeyError:
        return None
