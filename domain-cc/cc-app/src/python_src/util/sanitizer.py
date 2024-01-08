def sanitize(obj):
    if isinstance(obj, bool):
        return bool(str(obj).replace("\r\n", "").replace("\n", ""))
    if isinstance(obj, int):
        return int(str(obj).replace("\r\n", "").replace("\n", ""))
    return str(obj).replace("\r\n", "").replace("\n", "")
