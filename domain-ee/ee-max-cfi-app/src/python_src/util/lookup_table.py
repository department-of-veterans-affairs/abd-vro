import csv
import os

from .logger import logger


def get_max_ratings_by_code() -> dict[int, int]:
    """
    This function reads all CSV files in the 'data' directory that start with 'Max Ratings DC Lookup Table' followed by a version number. These files are
    sorted for consistency before importing them. Each CSV file is expected to have the following columns:
    - Diagnostic Code*
    - Rated Issue Name
    - Max Rating*
    - Body System
    - Category
    - Subcategory
    - CFR Reference
    * If the Diagnostic Code or Max Rating columns are empty, the row is skipped.

    As files are uploaded, if the diagnostic code is not in the dictionary, it will be added with its associated maximum rating. Any diagnostic codes that
    are already present will be updated with the new max rating.

    Returns:
        dict[int, int]: A dictionary where the keys are diagnostic codes and the values are the maximum ratings.
    """
    diagnostic_code_to_max_rating: dict[int, int] = {}

    data_files = sorted(os.listdir(os.path.join(os.path.dirname(__file__), 'data')))
    for file in data_files:
        if file.startswith('Max Ratings DC Lookup Table') and file.endswith('.csv'):
            ratings = import_ratings(file)
            msg = f'Loading {len(ratings)} ratings from "{file}"'
            updates = ratings & diagnostic_code_to_max_rating.keys()
            adds = updates ^ ratings.keys()
            if len(adds) > 0:
                msg += f' | Adds {len(adds)}'
            if len(updates) > 0:
                msg += f' | Updates {len(updates)}'
            logger.info(msg)
            diagnostic_code_to_max_rating.update(ratings)
    logger.info(f'Loaded {len(diagnostic_code_to_max_rating)} ratings from {len(data_files)} files')
    return diagnostic_code_to_max_rating


def import_ratings(filename: str) -> dict[int, int]:
    filename = os.path.join(os.path.dirname(__file__), 'data', filename)
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
