def sanitize(obj) -> str:
    return str(obj).replace('\r\n', '').replace('\n', '')
