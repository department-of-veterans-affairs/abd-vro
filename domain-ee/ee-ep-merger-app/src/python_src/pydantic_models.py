from uuid import UUID

from pydantic import BaseModel, conint
from src.python_src.service.merge_job import MergeJob


class MergeEndProductsRequest(BaseModel):
    pending_claim_id: conint(strict=True)
    supp_claim_id: conint(strict=True)


class MergeEndProductsResponse(BaseModel):
    job: MergeJob


class MergeEndProductsErrorResponse(BaseModel):
    job_id: UUID
    message: str
