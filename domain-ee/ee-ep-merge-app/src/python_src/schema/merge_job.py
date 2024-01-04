from datetime import datetime
from enum import Enum
from typing import Any
from uuid import UUID

import model.merge_job
from pydantic import BaseModel, ConfigDict, conint
from typing_extensions import ClassVar


class JobState(str, Enum):
    PENDING = 'PENDING'
    RUNNING_GET_PENDING_CLAIM = 'RUNNING_GET_PENDING_CLAIM'
    RUNNING_GET_PENDING_CLAIM_CONTENTIONS = 'RUNNING_GET_PENDING_CLAIM_CONTENTIONS'
    RUNNING_GET_EP400_CLAIM_CONTENTIONS = 'RUNNING_GET_EP400_CLAIM_CONTENTIONS'
    RUNNING_SET_TEMP_STATION_OF_JURISDICTION = 'RUNNING_SET_TEMP_STATION_OF_JURISDICTION'
    RUNNING_MERGE_CONTENTIONS = 'RUNNING_MERGE_CONTENTIONS'
    RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM = 'RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM'
    RUNNING_CANCEL_EP400_CLAIM = 'RUNNING_CANCEL_EP400_CLAIM'
    RUNNING_ADD_CLAIM_NOTE_TO_EP400 = 'RUNNING_ADD_CLAIM_NOTE_TO_EP400'
    COMPLETED_SUCCESS = 'COMPLETED_SUCCESS'

    RUNNING_GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE = 'RUNNING_GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE'
    RUNNING_GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE = 'RUNNING_GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE'
    RUNNING_SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE = 'RUNNING_SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE'
    RUNNING_MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE = 'RUNNING_MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE'
    RUNNING_MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION = 'RUNNING_MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION'
    RUNNING_CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION = 'RUNNING_CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION'
    COMPLETED_ERROR = 'COMPLETED_ERROR'

    @classmethod
    def incomplete_states(cls):
        return [state for state in cls if state != JobState.COMPLETED_SUCCESS and state != JobState.COMPLETED_ERROR]

    def __str__(self):
        return self.value


class MergeJob(BaseModel):
    _init_time: ClassVar[datetime] = datetime.now()

    job_id: UUID
    pending_claim_id: conint(strict=True)
    ep400_claim_id: conint(strict=True)
    state: JobState = JobState.PENDING
    error_state: JobState | None = None
    messages: list[Any] | None = None
    created_at: datetime = _init_time
    updated_at: datetime = _init_time

    model_config = ConfigDict(from_attributes=True)

    class Meta:
        orm_model = model.merge_job.MergeJob

    def error(self, messages):
        self.error_state = self.state
        self.state = JobState.COMPLETED_ERROR
        self.add_message(messages)

    def add_message(self, messages):
        if messages:
            if self.messages is None:
                self.messages = []
            msgs = [str(m) for m in messages] if isinstance(messages, list) else [str(messages)]
            self.messages.extend(msgs)

    def update(self, new_state: JobState):
        self.state = new_state
        self.updated_at = datetime.now()
