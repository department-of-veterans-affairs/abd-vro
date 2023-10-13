from model.get_contentions import Response


class MergeException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)


class ContentionsUtil:
    @staticmethod
    def merge_claims(pending_contentions: Response = None, supplemental_contentions: Response = None):
        if pending_contentions and supplemental_contentions:
            return [*pending_contentions.contentions, *supplemental_contentions.contentions]
        raise MergeException("Cannot merge contentions if pending or supplemental claim contentions are missing.")
