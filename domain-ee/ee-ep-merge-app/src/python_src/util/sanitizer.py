def sanitize(obj) -> str:
    if isinstance(obj, list):
        return str([sanitize(item) for item in obj])
    return str(obj).replace("\r\n", "").replace("\n", "")
