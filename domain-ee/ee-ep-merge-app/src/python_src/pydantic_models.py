from uuid import UUID

from pydantic import BaseModel, Field
from schema.merge_job import MergeJob
from typing_extensions import Annotated


class MergeEndProductsRequest(BaseModel):
    pending_claim_id: Annotated[int, Field(strict=True)]
    ep400_claim_id: Annotated[int, Field(strict=True)]


class MergeJobResponse(BaseModel):
    job: MergeJob


class MergeJobsResponse(BaseModel):
    jobs: list[MergeJob] = []


class MergeEndProductsErrorResponse(BaseModel):
    job_id: UUID
    message: str
