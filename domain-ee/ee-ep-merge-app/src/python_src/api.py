import asyncio
import logging
import sys
from contextlib import asynccontextmanager
from typing import Annotated
from uuid import UUID, uuid4

import uvicorn
from fastapi import BackgroundTasks, FastAPI, HTTPException, Query, status
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse
from pydantic_models import (
    MergeEndProductsErrorResponse,
    MergeEndProductsRequest,
    MergeJobResponse,
    MergeJobsResponse,
)
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine
from service.hoppy_service import HOPPY
from service.job_store import job_store
from util.sanitizer import sanitize


@asynccontextmanager
async def lifespan(api: FastAPI):
    await on_start_up()
    yield
    await on_shut_down()


async def on_start_up():
    loop = asyncio.get_event_loop()
    await HOPPY.start_hoppy_clients(loop)
    jobs_to_restart = job_store.get_all_incomplete_jobs()
    for job in jobs_to_restart:
        asyncio.get_event_loop().run_in_executor(None, resume_job_state_machine, job)


async def on_shut_down():
    await HOPPY.stop_hoppy_clients()


app = FastAPI(
    title="EP Merge Tool",
    description="Merge EP400 claim contentions into a pending claim (EP 010/020/110).",
    contact={},
    version="v0.1",
    license={
        "name": "CCO 1.0",
        "url": "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md"
    },
    servers=[
        {
            "url": "/ep",
            "description": "",
        },
    ],
    lifespan=lifespan
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
          response_model=MergeJobResponse,
          response_model_exclude_none=True)
async def merge_claims(merge_request: MergeEndProductsRequest, background_tasks: BackgroundTasks):
    validate_merge_request(merge_request)

    job_id = uuid4()
    logging.info(f"event=mergeJobSubmitted "
                 f"job_id={job_id} "
                 f"pending_claim_id={sanitize(merge_request.pending_claim_id)} "
                 f"ep400_claim_id={sanitize(merge_request.ep400_claim_id)}")

    merge_job = MergeJob(job_id=job_id,
                         pending_claim_id=merge_request.pending_claim_id,
                         ep400_claim_id=merge_request.ep400_claim_id)
    job_store.submit_merge_job(merge_job)

    background_tasks.add_task(start_job_state_machine, merge_job)

    return {"job": merge_job}


def validate_merge_request(merge_request: MergeEndProductsRequest):
    if merge_request.pending_claim_id == merge_request.ep400_claim_id:
        raise HTTPException(status_code=400, detail="Claim IDs must be different.")


def start_job_state_machine(merge_job):
    EpMergeMachine(merge_job).start()


def resume_job_state_machine(in_progress_job):
    if in_progress_job.state == JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM:
        in_progress_job.error("Job abandoned: Unable to verify if the contentions were successfully moved to pending EP")
        EpMergeMachine(in_progress_job, 'resume_processing_from_running_move_contentions_to_pending_claim').start()
    elif in_progress_job.state == JobState.RUNNING_CANCEL_EP400_CLAIM:
        EpMergeMachine(in_progress_job, 'resume_processing_from_running_cancel_ep400_claim').start()
    elif in_progress_job.state == JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400:
        EpMergeMachine(in_progress_job, 'resume_processing_from_running_add_note_to_ep400_claim').start()
    else:
        EpMergeMachine(in_progress_job, 'resume_restart').start()


@app.get("/merge/{job_id}",
         response_model=MergeJobResponse,
         responses={
             status.HTTP_200_OK: {"description": "Found job by job_id"},
             status.HTTP_404_NOT_FOUND: {"model": MergeEndProductsErrorResponse,
                                         "description": "Could not find job by job_id"}
         },
         response_model_exclude_none=True)
async def get_merge_request_by_job_id(job_id: UUID):
    job = job_store.get_merge_job(job_id)
    if job:
        logging.info(f"event=getMergeJobByJobId job={jsonable_encoder(job)}")
        return {"job": job}
    else:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND,
                            content=jsonable_encoder(
                                {"job_id": job_id,
                                 "message": "Could not find job"}))


@app.get("/merge",
         response_model=MergeJobsResponse,
         responses={
             status.HTTP_200_OK: {"description": "Find all jobs"}
         },
         response_model_exclude_none=True)
async def get_merge_jobs(state: Annotated[list[JobState], Query()] = JobState.incomplete_states(),
                         page: int = 1,
                         size: int = 10):
    jobs, total = job_store.query(states=state, offset=page, limit=size)
    logging.info(f"event=getMergeJobs "
                 f"total={total} "
                 f"page={sanitize(page)} "
                 f"size={sanitize(size)} "
                 f"states={sanitize(state)}")

    return MergeJobsResponse(states=state, total=total, page=page, size=size, jobs=jobs)


if __name__ == "__main__":
    uvicorn.run(app, host="localhost", port=8140)
