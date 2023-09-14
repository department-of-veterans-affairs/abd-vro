import logging
import sys
from uuid import UUID, uuid4

import service.ep_merger as ep_merger
from fastapi import BackgroundTasks, FastAPI, status
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse
from pydantic_models import (MergeEndProductsErrorResponse,
                             MergeEndProductsRequest, MergeEndProductsResponse)
from service.merge_job import MergeJob, get_merge_job, submit_merge_job
from util.sanitizer import sanitize

# TODO fill in empty fields
app = FastAPI(
    title="EP Merge Tool",
    description="",
    contact={},
    version="v0.1",
    license={
        "name": "CCO 1.0",
        "url": "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md"
    },
    servers=[
        {
            "url": "/end-product",
            "description": "",
        },
    ]
)

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    stream=sys.stdout,
)


@app.get("/health")
def get_health_status():
    return {"status": "ok"}


@app.post("/merge",
          status_code=status.HTTP_202_ACCEPTED,
          response_model=MergeEndProductsResponse,
          response_model_exclude_none=True)
async def merge_claims(merge_request: MergeEndProductsRequest, background_tasks: BackgroundTasks):
    if validate_merge_request(merge_request):
        job_id = uuid4()
        logging.info(f"event=mergeJobSubmitted "
                     f"job_id={job_id} "
                     f"pending_claim_id={sanitize(merge_request.pending_claim_id)} "
                     f"supp_claim_id={sanitize(merge_request.supp_claim_id)}")

        merge_job = MergeJob(job_id=job_id,
                             pending_claim_id=merge_request.pending_claim_id,
                             supp_claim_id=merge_request.supp_claim_id)
        submit_merge_job(merge_job)

        background_tasks.add_task(ep_merger.merge_end_products, merge_job)

        return jsonable_encoder({"job": merge_job})
    else:
        return {"status": status.HTTP_400_BAD_REQUEST}


def validate_merge_request(merge_request: MergeEndProductsRequest) -> bool:
    return True


@app.get("/merge/{job_id}",
         response_model=MergeEndProductsResponse,
         responses={
             status.HTTP_200_OK: {"description": "Found job by job_id"},
             status.HTTP_404_NOT_FOUND: {"model": MergeEndProductsErrorResponse,
                                         "description": "Could not find job by job_id"}
         },
         response_model_exclude_none=True)
async def get_merge_claims_status(job_id: UUID):
    job = get_merge_job(job_id)
    if job:
        logging.info(f"event=getMergeStatus {job}")
        return jsonable_encoder({"job": job})
    else:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND,
                            content=jsonable_encoder(
                                {"job_id": job_id,
                                 "message": "Could not find job"}))
