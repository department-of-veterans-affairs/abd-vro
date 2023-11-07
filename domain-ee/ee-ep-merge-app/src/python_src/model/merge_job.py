from enum import Enum
from typing import Any
from uuid import UUID

from pydantic import BaseModel, conint

JobState = Enum('JobState', [
    'COMPLETED_ERROR',
    'PENDING',
    'RUNNING_GET_PENDING_CLAIM',
    'RUNNING_GET_PENDING_CLAIM_CONTENTIONS',
    'RUNNING_GET_EP400_CLAIM_CONTENTIONS',
    'RUNNING_SET_TEMP_STATION_OF_JURISDICTION',
    'RUNNING_MERGE_CONTENTIONS',
    'RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS',
    'RUNNING_CANCEL_EP400_CLAIM',
    'COMPLETED_SUCCESS'])


class MergeJob(BaseModel):
    job_id: UUID
    pending_claim_id: conint(strict=True)
    ep400_claim_id: conint(strict=True)
    state: JobState = JobState.PENDING
    error_state: JobState | None = None
    messages: list[Any] | None = None

    def error(self, current_state, message):
        self.error_state = current_state
        self.state = JobState.COMPLETED_ERROR
        if self.messages is None:
            self.messages = []
        self.messages.append(message)
