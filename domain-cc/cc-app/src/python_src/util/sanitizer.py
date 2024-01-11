def sanitize_log(obj):
    """
    Removes all newlines and carriage returns from the input log statement. This
    prevents the CodeQL warning stemming from Log entries created from user input
    https://codeql.github.com/codeql-query-help/go/go-log-injection/
    """
    if isinstance(obj, bool):
        sanitized_str = str(obj).replace("\r\n", "").replace("\n", "")
        return sanitized_str == "True"
    if isinstance(obj, int):
        return int(str(obj).replace("\r\n", "").replace("\n", ""))
    return str(obj).replace("\r\n", "").replace("\n", "")
