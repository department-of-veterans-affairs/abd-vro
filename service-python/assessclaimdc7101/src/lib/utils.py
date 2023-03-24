from datetime import datetime


def docs_without_annotations_ids(event):
    """
    Robustly handle scenarios when documents without annotations is not set. In case the field is missing or None.

    :param event: MAS json body
    :return: list of strings
    """
    doc_ids = []
    if "documentsWithoutAnnotationsChecked" in list(event["evidence"].keys()):
        if event["evidence"]["documentsWithoutAnnotationsChecked"] is not None:
            doc_ids = event["evidence"]["documentsWithoutAnnotationsChecked"]
    return doc_ids


def extract_date(date_string):
    """
    Safely reading in a date by handling exceptions
    :param date_string: Date in expected format UTC Date Time.
    :return: Python class for datetime
    """
    try:
        date_entity = datetime.strptime(date_string, "%Y-%m-%dT%H:%M:%SZ").date()
    except ValueError:
        date_entity = datetime.today().date()

    return date_entity


def format_date(date_obj):
    """
    Reformat date object by rearranging and cutting out leading zeroes.
    :param date_obj:
    :return:
    """
    date_formatted = date_obj.strftime('X%m/X%d/%Y').replace('X0', 'X').replace('X', '')

    return date_formatted
