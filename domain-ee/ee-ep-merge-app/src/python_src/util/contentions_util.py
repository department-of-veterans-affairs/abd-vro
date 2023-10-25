from model.get_contentions import Response
from model.contention import ContentionSummary


class MergeException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)

class CompareException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)


class ContentionsUtil:
    @staticmethod
    def merge_claims(pending_contentions: Response = None, ep400_contentions: Response = None):
        if pending_contentions and ep400_contentions:
            return [*pending_contentions.contentions, *ContentionsUtil.new_contentions(pending_contentions.contentions, ep400_contentions.contentions)]
        raise MergeException("Cannot merge contentions if pending or EP400 claim contentions are missing.")

    @staticmethod
    def new_contentions(pending_contentions: list[ContentionSummary] = None, ep400_contentions: list[ContentionSummary] = None):
        if pending_contentions and ep400_contentions:
            pending = {(contention.contention_type_code, contention.claimant_text) for contention in pending_contentions}
            return [contention for contention in ep400_contentions if (contention.contention_type_code, contention.claimant_text) not in pending]
        raise CompareException("Cannot compare contentions if pending or EP400 claim contentions are missing.")
