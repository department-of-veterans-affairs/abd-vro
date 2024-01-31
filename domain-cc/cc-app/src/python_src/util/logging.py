import json
import logging
import time

from .util.logging_dropdown_selections import build_logging_table
from .util.sanitizer import sanitize_log

dropdown_values = build_logging_table()


def log_lookup_table_match(
    classification_code: int,
    contention_text: str,
):
    is_in_dropdown = contention_text.strip().lower() in dropdown_values
    log_as_json({"is_in_dropdown": sanitize_log(is_in_dropdown)})
    log_contention_text = contention_text if is_in_dropdown else "Not in dropdown"

    if classification_code:
        already_mapped_text = contention_text.strip().lower()  # do not leak PII
        log_as_json({"lookup_table_match": sanitize_log(already_mapped_text)})
    elif is_in_dropdown:
        log_as_json(
            {
                "lookup_table_match": sanitize_log(
                    f"No table match for {log_contention_text}"
                )
            }
        )
    else:
        log_as_json(
            {"lookup_table_match": sanitize_log("No table match for free text entry")}
        )


def log_as_json(log: dict):
    if "date" not in log.keys():
        log.update({"date": time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())})
    if "level" not in log.keys():
        log.update({"level": "info"})
    logging.info(json.dumps(log))
