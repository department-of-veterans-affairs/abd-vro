class MergeException(BaseException):
    def __init__(self, message=None):
        self.message = message
        super().__init__(self.message)


class ClaimsUtil:

    @staticmethod
    def merge_claims(pending_claim, supplemental_claim):
        # TODO fill in merging
        pass
