import logging
from enum import Enum
from uuid import UUID

from pydantic import BaseModel, conint


class NextEnum(str, Enum):
    def next(self):
        members = [member for member in NextEnum]
        cur_index = members.index(self)
        if cur_index + 1 < len(members):
            next_member = NextEnum(members[cur_index + 1])
            logging.info(next_member)
            return next_member
        else:
            raise ValueError("No more members.")


class JobState(NextEnum):
    ERROR = 'ERROR'
    PENDING = 'PENDING'
    RUNNING_SET_TEMP_STATION_OF_JURISDICTION = 'RUNNING_SET_TEMP_STATION_OF_JURISDICTION'
    RUNNING_GET_PENDING_CLAIM_CONTENTIONS = 'RUNNING_GET_PENDING_CLAIM_CONTENTIONS'
    RUNNING_GET_SUPP_CLAIM_CONTENTIONS = 'RUNNING_GET_SUPP_CLAIM_CONTENTIONS'
    RUNNING_MERGE_CONTENTIONS = 'RUNNING_MERGE_CONTENTIONS'
    RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS = 'RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS'
    COMPLETED = 'COMPLETED'

    def next(self):
        states = [state for state in JobState]
        cur_index = states.index(self)
        if cur_index + 1 < len(states):
            job_state = JobState(states[cur_index + 1])
            return job_state
        else:
            raise ValueError("No more states.")


class MergeJob(BaseModel):
    job_id: UUID
    pending_claim_id: conint(strict=True)
    supp_claim_id: conint(strict=True)
    state: JobState = JobState.PENDING
    message: str | None = None

    def next_state(self):
        self.state = self.state.next()
        logging.info(f"event=jobProgressed job_id={self.job_id} state={self.state.value}")

    def error(self, message):
        self.state = JobState.ERROR
        self.message = message


JOB_MAP = {}


def get_merge_jobs():
    return JOB_MAP


def get_merge_job(job_id: UUID):
    return JOB_MAP.get(job_id)


def submit_merge_job(merge_job: MergeJob):
    JOB_MAP[merge_job.job_id] = merge_job
