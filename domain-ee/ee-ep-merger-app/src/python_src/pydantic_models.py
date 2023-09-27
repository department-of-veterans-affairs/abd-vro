from uuid import UUID

from pydantic import BaseModel, Field
from src.python_src.service.merge_job import MergeJob
from typing_extensions import Annotated


class MergeEndProductsRequest(BaseModel):
    pending_claim_id: Annotated[int, Field(strict=True)]
    supp_claim_id: Annotated[int, Field(strict=True)]


class MergeEndProductsResponse(BaseModel):
    job: MergeJob


class MergeEndProductsErrorResponse(BaseModel):
    job_id: UUID
    message: str
