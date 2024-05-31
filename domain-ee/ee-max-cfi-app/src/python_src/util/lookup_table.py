import csv
import os

from .table_version import TABLE_VERSION

TABLE_NAME = f'Max Ratings DC Lookup Table {TABLE_VERSION}.csv'


def get_max_ratings_by_code() -> dict[int, int]:
    filename = os.path.join(os.path.dirname(__file__), 'data', TABLE_NAME)
    diagnostic_code_to_max_rating: dict[int, int] = {}
    with open(filename, 'r') as file:
        csv_reader = csv.reader(file)
        for index, csv_line in enumerate(csv_reader):
            if index == 0:
                continue

            if len(csv_line) != 7:
                raise ValueError(f'Invalid CSV line at index {index}')

            diagnostic_code_str, rated_issue_name, max_rating_str, body_system, category, subcategory, cfr_ref = csv_line
            if not diagnostic_code_str or not max_rating_str:
                continue

            try:
                diagnostic_code = int(diagnostic_code_str)
                max_rating = int(max_rating_str)
            except ValueError:
                raise ValueError(f'Invalid diagnostic code or max rating at index {index}: \n{csv_line}')

            diagnostic_code_to_max_rating[diagnostic_code] = max_rating
    return diagnostic_code_to_max_rating


MAX_RATINGS_BY_CODE = get_max_ratings_by_code()


def get_max_rating(diagnostic_code: int) -> int | None:
    return MAX_RATINGS_BY_CODE.get(diagnostic_code)
