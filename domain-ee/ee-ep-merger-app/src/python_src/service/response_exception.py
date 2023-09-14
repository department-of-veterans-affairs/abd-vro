class ResponseException(Exception):
    """Raised when the request could not be made."""

    def __init__(self, message, correlation_id):
        self.message = message
        self.correlation_id = correlation_id
        super().__init__(self.message)
