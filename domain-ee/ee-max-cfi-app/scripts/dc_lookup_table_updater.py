import csv
from typing import Any

DATA_FILE = '../src/python_src/util/data/Diagnostic Code Lookup Table.csv'
NEW_DATA_FILE = 'Max Ratings DC Lookup Table v2.csv'


def consolidate_data_files() -> dict[int, tuple[Any, Any, Any, Any, Any, Any]] | None:
    original_data = import_file(DATA_FILE)
    data_map: dict[int, tuple[Any, int, Any, Any, Any, Any]] = original_data.copy()

    imported_data = import_file(NEW_DATA_FILE)
    updates = imported_data & data_map.keys()
    adds = updates ^ imported_data.keys()
    msg = f'Loading {len(imported_data)} ratings from "{NEW_DATA_FILE}" | Adds {len(adds)} | Potential Updates {len(updates)}'
    print(msg)

    data_map.update(imported_data)

    new_data = dict(sorted(data_map.items()))

    if new_data == original_data:
        print('No changes detected')
        return None

    return new_data


def import_file(filename: str) -> dict[int, tuple[Any, Any, Any, Any, Any, Any]]:
    diagnostic_code_to_data: dict[int, tuple[Any, Any, Any, Any, Any, Any]] = {}
    with open(filename, 'r') as file:
        csv_reader = csv.reader(file)
        for index, csv_line in enumerate(csv_reader):
            if index == 0:
                continue

            if len(csv_line) != 7:
                raise ValueError(f'Invalid CSV line at index {index}')

            diagnostic_code_str, rated_issue_name, max_rating_str, body_system, category, subcategory, cfr_ref = csv_line

            try:
                diagnostic_code = int(diagnostic_code_str)
            except ValueError:
                raise ValueError(f'Invalid diagnostic code at index {index}: \n{csv_line}')

            diagnostic_code_to_data[diagnostic_code] = (rated_issue_name, max_rating_str, body_system, category, subcategory, cfr_ref)

    return diagnostic_code_to_data


def export_data(data: dict[int, tuple[Any, int, Any, Any, Any, Any]]):
    with open(DATA_FILE, 'w') as file:
        csv_writer = csv.writer(file)
        csv_writer.writerow(['Diagnostic Code', 'Rated Issue Name', 'Max Rating', 'Body System', 'Category', 'Subcategory', 'CFR Reference'])
        for diagnostic_code, data_tuple in data.items():
            csv_writer.writerow([diagnostic_code, *data_tuple])


def increment_table_version():
    with open('../src/python_src/util/data/table_version.py', 'r') as file:
        version = int(file.readline().strip().split(' = ').pop())
    with open('../src/python_src/util/data/table_version.py', 'w') as file:
        file.write(f'TABLE_VERSION = {version + 1}\n')


if __name__ == '__main__':
    data = consolidate_data_files()
    if data:
        export_data(data)
        print('Data exported')
        increment_table_version()
        print('Table version incremented')
