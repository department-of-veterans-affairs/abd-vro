from model.get_contentions import Response


class MergeException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)


class ContentionsUtil:
    @staticmethod
    def merge_claims(pending_contentions: Response = None, ep400_contentions: Response = None):
        if pending_contentions and ep400_contentions:
            return [*pending_contentions.contentions, *ep400_contentions.contentions]
        raise MergeException("Cannot merge contentions if pending or EP400 claim contentions are missing.")
