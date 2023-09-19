class ResponseException(Exception):
    """Raised when the request could not be made."""

    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)
