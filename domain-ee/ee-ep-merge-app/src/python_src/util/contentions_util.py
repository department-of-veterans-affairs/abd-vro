from schema.contention import ContentionSummary, ExistingContention


class ContentionsUtil:

    @staticmethod
    def new_contentions(pending_contentions: list[ContentionSummary] = None, ep400_contentions: list[ContentionSummary] = None):
        if not pending_contentions and not ep400_contentions:
            return []
        if not ep400_contentions:
            return []
        if not pending_contentions:
            return ep400_contentions

        pending = {(contention.contention_type_code, contention.claimant_text) for contention in pending_contentions}
        return [contention for contention in ep400_contentions if (contention.contention_type_code, contention.claimant_text) not in pending]

    @staticmethod
    def to_existing_contentions(contention_summaries: list[ContentionSummary] = None):
        return [ExistingContention.model_validate(summary.model_dump()) for summary in contention_summaries]
