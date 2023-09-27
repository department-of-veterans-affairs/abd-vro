class MergeException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)


class ContentionsUtil:

    @staticmethod
    def merge_claims(pending_contentions, supplemental_contentions):
        if pending_contentions and supplemental_contentions:
            # TODO fill in merging
            return []
        raise MergeException("Cannot merge contentions if pending or supplemental claim contentions are missing.")
