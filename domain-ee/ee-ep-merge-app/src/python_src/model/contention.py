from datetime import datetime

from pydantic import BaseModel, ConfigDict, field_serializer
from pydantic.alias_generators import to_camel


class TrackedItemAssociation(BaseModel):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    tracked_item_id: int


class ContentionSummary(BaseModel):
    model_config = ConfigDict(populate_by_name=True, alias_generator=to_camel)

    medical_ind: bool
    begin_date: datetime
    create_date: datetime | None = None
    alt_contention_name: str | None = None
    completed_date: datetime | None = None
    notification_date: datetime | None = None
    contention_type_code: str
    classification_type: int | None = None
    diagnostic_type_code: str | None = None
    claimant_text: str
    contention_status_type_code: str | None = None
    original_source_type_code: str | None = None
    special_issue_codes: list[str] | None = None
    associated_tracked_items: list[TrackedItemAssociation] | None = None
    contention_id: int
    last_modified: datetime
    lifecycle_status: str | None = None
    action: str | None = None
    automation_indicator: bool | None = None
    summary_date_time: datetime | None = None

    @field_serializer('begin_date', 'create_date', 'completed_date', 'notification_date', 'last_modified')
    def serialize_datetime(self, dt: datetime, _info):
        if dt is not None:
            return dt.isoformat()
