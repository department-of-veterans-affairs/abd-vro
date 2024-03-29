from uuid import UUID

from pydantic import BaseModel, Field
from schema.merge_job import JobState, MergeJob
from typing_extensions import Annotated


class HealthResponse(BaseModel):
    status: str
    errors: list[str] | None = None


class MergeEndProductsRequest(BaseModel):
    pending_claim_id: Annotated[int, Field(strict=True)]
    ep400_claim_id: Annotated[int, Field(strict=True)]


class MergeJobResponse(BaseModel):
    job: MergeJob


class MergeJobsResponse(BaseModel):
    states: list[JobState]
    total: int
    page: int
    size: int
    jobs: list[MergeJob] = []


class MergeEndProductsErrorResponse(BaseModel):
    job_id: UUID
    message: str
