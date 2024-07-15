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
CREATED_AT = NOW
UPDATED_AT = NOW + timedelta(seconds=1)


def create_job(job_id, state: JobState, error_state: JobState | None = None, created_at: datetime = CREATED_AT, updated_at: datetime = UPDATED_AT):
    JOB_IDS.append(str(job_id))
    return {
        'job_id': job_id,
        'pending_claim_id': PENDING_ID,
        'ep400_claim_id': EP400_ID,
        'state': state,
        'error_state': error_state,
        'created_at': created_at.isoformat(),
        'updated_at': updated_at.isoformat(),
    }


def assert_response(response, expected_state: JobState, status_code: int = 200):
    assert response.status_code == status_code
    response_json = response.json()
    assert response_json is not None
    assert response_json['job']['pending_claim_id'] == PENDING_ID
    assert response_json['job']['ep400_claim_id'] == EP400_ID
    assert response_json['job']['state'] == expected_state
    assert response_json['job']['created_at'] == CREATED_AT.isoformat()
    assert response_json['job']['updated_at'] == UPDATED_AT.isoformat()
    return response_json


@pytest.fixture(scope='session')
def submit_jobs():
    JOB_STORE.submit_merge_job(create_job(JOB_ID, JobState.COMPLETED_SUCCESS))
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.COMPLETED_ERROR))
    for state in JobState.incomplete_states():
        JOB_STORE.submit_merge_job(create_job(uuid4(), state))


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


def generate_query_params(states):
    if states:
        return f'size={len(states)}&state=' + '&state='.join([str(state) for state in states])
    return ''


@pytest.mark.asyncio(scope='session')
@pytest.mark.parametrize(
    'states',
    [
        pytest.param(JobState.incomplete_states(), id='incomplete states'),
        pytest.param([JobState.PENDING, JobState.GET_PENDING_CLAIM], id='multiple states'),
        pytest.param([JobState.COMPLETED_ERROR, JobState.COMPLETED_SUCCESS], id='completed states'),
    ],
)
async def test_get_jobs_by_state(submit_jobs, states: list[JobState]):
    params = generate_query_params(states)
    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?{params}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['states'] == states
        assert json['total'] == len(states)
        assert json['page'] == 1
        assert json['size'] == len(states)
        assert len(json['jobs']) == len(states)

        if states:
            for job_json in json['jobs']:
                assert job_json['job_id'] in JOB_IDS


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_updated_at_start_found():
    created_at = datetime.now()
    updated_at = created_at + timedelta(seconds=1)
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.PENDING, created_at=created_at, updated_at=updated_at))

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?updated_at_start={created_at.isoformat()}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 1
        assert len(json['jobs']) == 1
        assert json['jobs'][0]['job_id'] in JOB_IDS
        assert json['jobs'][0]['state'] == JobState.PENDING


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_updated_at_start_not_found():
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.PENDING))

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?updated_at_start={(NOW + timedelta(days=1)).isoformat()}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 0
        assert len(json['jobs']) == 0


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_updated_at_end_found():
    created_at = NOW - timedelta(days=1)
    updated_at = created_at + timedelta(seconds=1)
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.PENDING, created_at=created_at, updated_at=updated_at))

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?updated_at_end={updated_at.isoformat()}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 1
        assert len(json['jobs']) == 1
        assert json['jobs'][0]['job_id'] in JOB_IDS
        assert json['jobs'][0]['state'] == JobState.PENDING


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_updated_at_end_not_found():
    created_at = NOW - timedelta(days=1)
    updated_at = created_at + timedelta(seconds=1)
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.PENDING, created_at=created_at, updated_at=updated_at))

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?updated_at_end={created_at.isoformat()}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 0
        assert len(json['jobs']) == 0


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_error_state_not_found():
    states = JobState.incomplete_states()
    query = 'error_state=' + '&error_state='.join([str(state) for state in states])

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?{query}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 0
        assert len(json['jobs']) == 0


@pytest.mark.asyncio(scope='session')
async def test_get_jobs_by_error_state_found():
    error_state = JobState.GET_PENDING_CLAIM
    JOB_STORE.submit_merge_job(create_job(uuid4(), JobState.COMPLETED_ERROR, error_state=error_state))

    async with AsyncClient(app=app, base_url='http://test') as client:
        response = await client.get(url=f'/merge?error_state={error_state}')
        assert response.status_code == 200
        json = response.json()
        assert json is not None
        assert json['total'] == 1
        assert len(json['jobs']) == 1
        assert json['jobs'][0]['job_id'] in JOB_IDS
        assert json['jobs'][0]['state'] == JobState.COMPLETED_ERROR
        assert json['jobs'][0]['error_state'] == error_state
