from datetime import datetime, timedelta
from uuid import uuid4

import pytest
from httpx import AsyncClient

from src.python_src.api import app
from src.python_src.schema.merge_job import JobState
from src.python_src.service.job_store import JOB_STORE

NOW = datetime.now()
JOB_IDS = []

JOB_ID = uuid4()
PENDING_ID = 1
EP400_ID = 2
CREATED_AT = NOW.isoformat()
UPDATED_AT = (NOW + timedelta(seconds=1)).isoformat()


def create_job(state, job_id):
    JOB_IDS.append(str(job_id))
    return {
        'job_id': job_id,
        'pending_claim_id': PENDING_ID,
        'ep400_claim_id': EP400_ID,
        'state': state,
        'created_at': CREATED_AT,
        'updated_at': UPDATED_AT,
    }


def assert_response(response, expected_state: JobState, status_code: int = 200):
    assert response.status_code == status_code
    response_json = response.json()
    assert response_json is not None
    assert response_json['job']['pending_claim_id'] == PENDING_ID
    assert response_json['job']['ep400_claim_id'] == EP400_ID
    assert response_json['job']['state'] == expected_state
    assert response_json['job']['created_at'] == CREATED_AT
    assert response_json['job']['updated_at'] == UPDATED_AT
    return response_json


@pytest.fixture(scope='session')
def submit_jobs():
    JOB_STORE.submit_merge_job(create_job(JobState.COMPLETED_SUCCESS, JOB_ID))
    JOB_STORE.submit_merge_job(create_job(JobState.COMPLETED_ERROR, uuid4()))
    for state in JobState.incomplete_states():
        JOB_STORE.submit_merge_job(create_job(state, uuid4()))


@pytest.mark.asyncio(scope='session')
async def test_get_job_by_id(submit_jobs):
    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge/{JOB_ID}')
        assert_response(response, JobState.COMPLETED_SUCCESS)


@pytest.mark.asyncio(scope='session')
async def test_get_job_by_id_not_found(submit_jobs):
    job_id = uuid4()
    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge/{job_id}')
        assert response.status_code == 404


def generate_query_params(states: list[JobState]):
    if not states:
        return ''
    return '&state=' + '&state='.join([state.name for state in states])


@pytest.mark.asyncio(scope='session')
@pytest.mark.parametrize(
    'states',
    [
        pytest.param([], id='incomplete states'),
        pytest.param([JobState.PENDING, JobState.GET_PENDING_CLAIM], id='multiple states'),
        pytest.param([JobState.COMPLETED_ERROR, JobState.COMPLETED_SUCCESS], id='completed states'),
    ],
)
async def test_get_jobs_by_state(submit_jobs, states: list[JobState]):
    params = generate_query_params(states)
    if not states:
        states = JobState.incomplete_states()
    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?size={len(states)}{params}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['states'] == states
        assert json['total'] == len(states)
        assert json['page'] == 1
        assert json['size'] == len(states)
        assert len(json['jobs']) == len(states)

        for job_json in json['jobs']:
            assert job_json['job_id'] in JOB_IDS
