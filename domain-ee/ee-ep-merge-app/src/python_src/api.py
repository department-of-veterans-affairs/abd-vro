import asyncio
import logging
import sys
from contextlib import asynccontextmanager
from datetime import datetime
from typing import Annotated, AsyncIterator
from uuid import UUID, uuid4

import uvicorn
from fastapi import BackgroundTasks, FastAPI, HTTPException, Query, Request, status
from fastapi.encoders import jsonable_encoder
from pydantic_models import (
    HealthResponse,
    MergeEndProductsErrorResponse,
    MergeEndProductsRequest,
    MergeJobResponse,
    MergeJobsResponse,
)
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine
from service.hoppy_service import HOPPY
from service.job_runner import JOB_RUNNER
from service.job_store import JOB_STORE
from sqlalchemy.exc import SQLAlchemyError
from starlette.responses import JSONResponse
from util.sanitizer import sanitize

CONNECT_TO_DATABASE_FAILURE = 'Cannot connect to database.'
CONNECT_TO_RABBIT_MQ_FAILURE = 'Cannot connect to RabbitMQ.'


@asynccontextmanager
async def lifespan(api: FastAPI) -> AsyncIterator[None]:
    on_start_up()
    yield
    await on_shut_down()


def on_start_up() -> None:
    loop = asyncio.get_event_loop()
    loop.create_task(start_job_runner())
    loop.create_task(start_hoppy())


async def start_job_runner() -> None:
    await JOB_RUNNER.start()


async def start_hoppy() -> None:
    await HOPPY.start_hoppy_clients()


async def on_shut_down() -> None:
    await HOPPY.stop_hoppy_clients()


app = FastAPI(
    title='EP Merge Tool',
    description='Merge EP400 claim contentions into a pending claim (EP 010/020/110).',
    contact={},
    version='v0.1',
    license={'name': 'CCO 1.0', 'url': 'https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md'},
    servers=[
        {
            'url': '/ep',
            'description': '',
        },
    ],
    lifespan=lifespan,
)

logging.basicConfig(
    format='[%(asctime)s] %(levelname)-8s %(message)s',
    level=logging.INFO,
    datefmt='%Y-%m-%d %H:%M:%S',
    stream=sys.stdout,
)


@app.get('/health', response_model=HealthResponse, response_model_exclude_none=True)
def get_health_status():
    errors = health_check_errors()
    if errors:
        return JSONResponse(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, content={'status': 'unhealthy', 'errors': errors})
    else:
        return {'status': 'healthy'}


def health_check_errors() -> list[str]:
    errors = []
    if not HOPPY.is_ready():
        errors.append(CONNECT_TO_RABBIT_MQ_FAILURE)
    if not JOB_STORE.is_ready():
        errors.append(CONNECT_TO_DATABASE_FAILURE)
    return errors


@app.post('/merge', status_code=status.HTTP_202_ACCEPTED, response_model=MergeJobResponse, response_model_exclude_none=True)
async def merge_claims(request: Request, merge_request: MergeEndProductsRequest, background_tasks: BackgroundTasks) -> MergeJobResponse | JSONResponse:
    validate_merge_request(merge_request)

    errors = health_check_errors()
    if errors:
        logging.error(f'event=mergeJobRejected errors={errors}')
        return JSONResponse(status_code=500, content=jsonable_encoder({'method': 'POST', 'url': str(request.url), 'errors': errors}))

    job_id = uuid4()

    merge_job = MergeJob(job_id=job_id, pending_claim_id=merge_request.pending_claim_id, ep400_claim_id=merge_request.ep400_claim_id)
    JOB_STORE.submit_merge_job(merge_job)

    logging.info(
        f'event=mergeJobSubmitted '
        f'job_id={job_id} '
        f'pending_claim_id={sanitize(merge_request.pending_claim_id)} '
        f'ep400_claim_id={sanitize(merge_request.ep400_claim_id)}'
    )

    background_tasks.add_task(JOB_RUNNER.start_job, merge_job)

    return MergeJobResponse(job=merge_job)


def validate_merge_request(merge_request: MergeEndProductsRequest) -> None:
    if merge_request.pending_claim_id == merge_request.ep400_claim_id:
        raise HTTPException(status_code=400, detail='Claim IDs must be different.')


def start_job_state_machine(merge_job):
    EpMergeMachine(merge_job).start()


@app.get(
    '/merge/{job_id}',
    response_model=MergeJobResponse,
    responses={
        status.HTTP_200_OK: {'description': 'Found job by job_id'},
        status.HTTP_404_NOT_FOUND: {'model': MergeEndProductsErrorResponse, 'description': 'Could not find job by job_id'},
    },
    response_model_exclude_none=True,
)
async def get_merge_request_by_job_id(job_id: UUID) -> MergeJobResponse | JSONResponse:
    job = JOB_STORE.get_merge_job(job_id)
    if job:
        logging.info(f'event=getMergeJobByJobId job={jsonable_encoder(job)}')
        return MergeJobResponse(job=job)
    else:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content=jsonable_encoder({'job_id': job_id, 'message': 'Could not find job'}))


@app.get(
    '/merge',
    response_model=MergeJobsResponse,
    responses={status.HTTP_200_OK: {'description': 'Find all jobs matching the query parameters'}},
    response_model_exclude_none=False,
)
async def get_merge_jobs(
    state: Annotated[list[JobState], Query(title='the states to filter the query by')] = None,
    error_state: Annotated[list[JobState], Query(title='the error states to filter the query by')] = None,
    updated_at_start: Annotated[datetime, Query(title='start of timeframe query filter of the datetime the job was last updated')] = None,
    updated_at_end: Annotated[datetime, Query(title='end of timeframe query filter of the datetime the job was last updated')] = None,
    page: Annotated[int, Query(title='the page of results to return', ge=1)] = 1,
    size: Annotated[int, Query(title='the number of results per page', ge=1)] = 10,
) -> MergeJobsResponse:
    jobs, total = JOB_STORE.query(
        states=state, error_states=error_state, offset=page, limit=size, updated_at_start=updated_at_start, updated_at_end=updated_at_end
    )

    logging.info(
        f'event=getMergeJobs total={total} '
        f'page={sanitize(page)} '
        f'size={sanitize(size)} '
        f'updated_at_start={sanitize(updated_at_start.isoformat()) if updated_at_start else None} '
        f'updated_at_end={sanitize(updated_at_end.isoformat()) if updated_at_end else None} '
        f'states={[str(s) for s in state] if state else None} '
        f'error_states={[str(s) for s in error_state] if error_state else None}'
    )

    return MergeJobsResponse(
        states=state, error_states=error_state, updated_at_start=updated_at_start, updated_at_end=updated_at_end, total=total, page=page, size=size, jobs=jobs
    )


@app.exception_handler(SQLAlchemyError)
async def sqlalchemy_exception_handler(request: Request, err: SQLAlchemyError) -> JSONResponse:
    msg = str(err).replace('\n', ' ')
    logging.error(f"event=requestFailed method={request.method} url={request.url} resource={'Database'} error={msg}")
    return JSONResponse(status_code=500, content=jsonable_encoder({'method': request.method, 'url': str(request.url), 'errors': [CONNECT_TO_DATABASE_FAILURE]}))


if __name__ == '__main__':
    uvicorn.run(app, host='localhost', port=8140)
