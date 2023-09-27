from enum import Enum
from typing import Any
from uuid import UUID

from pydantic import BaseModel, conint


class JobState(str, Enum):
    COMPLETED_ERROR = 'COMPLETED_ERROR'
    PENDING = 'PENDING'
    RUNNING_SET_TEMP_STATION_OF_JURISDICTION = 'RUNNING_SET_TEMP_STATION_OF_JURISDICTION'
    RUNNING_GET_PENDING_CLAIM_CONTENTIONS = 'RUNNING_GET_PENDING_CLAIM_CONTENTIONS'
    RUNNING_GET_SUPP_CLAIM_CONTENTIONS = 'RUNNING_GET_SUPP_CLAIM_CONTENTIONS'
    RUNNING_MERGE_CONTENTIONS = 'RUNNING_MERGE_CONTENTIONS'
    RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS = 'RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS'
    RUNNING_CANCEL_SUPP_CLAIM = 'RUNNING_CANCEL_SUPP_CLAIM'
    COMPLETED_SUCCESS = 'COMPLETED_SUCCESS'


class MergeJob(BaseModel):
    job_id: UUID
    pending_claim_id: conint(strict=True)
    supp_claim_id: conint(strict=True)
    state: JobState = JobState.PENDING
    error_state: JobState | None = None
    messages: list[Any] | None = None

    def error(self, current_state, message):
        self.error_state = current_state
        self.state = JobState.COMPLETED_ERROR
        if self.messages is None:
            self.messages = []
        self.messages.append(message)
