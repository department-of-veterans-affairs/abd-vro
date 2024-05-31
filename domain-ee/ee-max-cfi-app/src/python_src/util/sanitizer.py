from typing import Any


def sanitize(obj: Any) -> str:
    return str(obj).replace('\r\n', '').replace('\n', '')
