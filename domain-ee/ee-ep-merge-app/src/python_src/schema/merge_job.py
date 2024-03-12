from datetime import datetime
from enum import auto
from typing import Any
from uuid import UUID

import model.merge_job
from pydantic import BaseModel, ConfigDict, conint, Field
from util.custom_enum import StrEnum


class JobState(StrEnum):
    PENDING = auto()
    GET_PENDING_CLAIM = auto()
    GET_PENDING_CLAIM_CONTENTIONS = auto()
    GET_EP400_CLAIM_CONTENTIONS = auto()
    SET_TEMP_STATION_OF_JURISDICTION = auto()
    MERGE_CONTENTIONS = auto()
    MOVE_CONTENTIONS_TO_PENDING_CLAIM = auto()
    CANCEL_EP400_CLAIM = auto()
    ADD_CLAIM_NOTE_TO_EP400 = auto()
    COMPLETED_SUCCESS = auto()

    GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE = auto()
    GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE = auto()
    SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE = auto()
    MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE = auto()
    MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION = auto()
    CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION = auto()
    COMPLETED_ERROR = auto()

    @classmethod
    def incomplete_states(cls) -> list[str]:
        return [state.name for state in cls if state != JobState.COMPLETED_SUCCESS and state != JobState.COMPLETED_ERROR]

    def __str__(self):
        return self.value


class MergeJob(BaseModel):

    job_id: UUID
    pending_claim_id: conint(strict=True)
    ep400_claim_id: conint(strict=True)
    state: JobState = JobState.PENDING
    error_state: JobState | None = None
    messages: list[Any] | None = None
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = None

    def __init__(self, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        if not self.updated_at:
            self.updated_at = self.created_at

    model_config = ConfigDict(from_attributes=True)

    class Meta:
        orm_model = model.merge_job.MergeJob

    def error(self, message: dict[Any, Any]):
        self.error_state = self.state
        self.state = JobState.COMPLETED_ERROR
        self.add_message(message)

    def add_message(self, message: dict[Any, Any]):
        if message:
            if self.messages is None:
                self.messages = []
            self.messages.append(message)

    def update(self, new_state: JobState):
        self.state = new_state
        self.updated_at = datetime.now()
