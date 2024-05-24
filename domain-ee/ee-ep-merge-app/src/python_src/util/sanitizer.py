from typing import Any


def sanitize(obj: Any) -> str:
    if isinstance(obj, list):
        return str([sanitize(item) for item in obj])
    return str(obj).replace('\r\n', '').replace('\n', '')
