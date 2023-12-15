from uuid import UUID

from schema.merge_job import MergeJob
from pydantic import BaseModel, Field
from typing_extensions import Annotated


class MergeEndProductsRequest(BaseModel):
    pending_claim_id: Annotated[int, Field(strict=True)]
    ep400_claim_id: Annotated[int, Field(strict=True)]


class MergeEndProductsResponse(BaseModel):
    job: MergeJob


class MergeEndProductsErrorResponse(BaseModel):
    job_id: UUID
    message: str
