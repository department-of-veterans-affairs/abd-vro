import asyncio

import pytest
from httpx import AsyncClient
from src.python_src.api import app
from src.python_src.schema.merge_job import JobState
from src.python_src.service.job_store import JOB_STORE

ACCEPTABLE_JOB_PROCESSING_DURATION = 0.2

PENDING_CLAIM_ID = 10000
EP400_WITH_DUPLICATE = 10001
EP400_WITH_DIFFERENT_CONTENTION_TYPE_CODE = 10002
EP400_WITH_DIFFERENT_CLAIMANT_TEXT = 10003
EP400_WITH_MULTI_CONTENTION_ONE_DUPLICATE = 10004
EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES = 10005

CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS = 5001
CLAIM_ID_ERROR_AT_CANCEL_CLAIM = 5002
CLAIM_ID_ERROR_AT_SET_TSOJ = 5003
CLAIM_ID_ERROR_AT_GET_CONTENTIONS = 5004
CLAIM_ID_ERROR_AT_UPDATE_CONTENTIONS = 5005
CLAIM_ID_ERROR_AT_CREATE_CONTENTIONS = 5006


def assert_response(
    response,
    pending_claim_id,
    ep400_claim_id,
    expected_state: JobState,
    expected_error_state: JobState | None = None,
    expected_num_errors: int = 0,
    status_code: int = 200,
):
    assert response.status_code == status_code
    response_json = response.json()
    assert response_json is not None
    job = response_json['job']
    assert job['pending_claim_id'] == pending_claim_id
    assert job['ep400_claim_id'] == ep400_claim_id
    assert job['state'] == expected_state
    if expected_error_state is None:
        assert 'error_state' not in job
    else:
        assert job['error_state'] == expected_error_state
    if expected_num_errors == 0:
        assert 'messages' not in job
    else:
        assert len(job['messages']) == expected_num_errors
    return response_json


def assert_job(job_id, pending_claim_id, ep400_claim_id, expected_state: JobState, expected_error_state: JobState | None = None, expected_num_errors: int = 0):
    job = JOB_STORE.get_merge_job(job_id)
    assert job is not None
    assert job.pending_claim_id == pending_claim_id
    assert job.ep400_claim_id == ep400_claim_id
    assert job.state == expected_state
    assert job.error_state == expected_error_state

    if expected_num_errors == 0:
        assert job.messages is None
    else:
        assert len(job.messages) == expected_num_errors


def assert_response_and_job(
    response,
    pending_claim_id,
    ep400_claim_id,
    expected_state: JobState,
    expected_error_state: JobState | None = None,
    expected_num_errors: int = 0,
    status_code: int = 200,
):
    json = assert_response(response, pending_claim_id, ep400_claim_id, expected_state, expected_error_state, expected_num_errors, status_code)
    job_id = json['job']['job_id']
    assert_job(job_id, pending_claim_id, ep400_claim_id, expected_state, expected_error_state, expected_num_errors)


async def submit_request_and_process(client, pending_claim_id, ep400_claim_id):
    request = {"pending_claim_id": pending_claim_id, "ep400_claim_id": ep400_claim_id}
    response = await client.post(url="/merge", json=request)

    response_json = assert_response(response, pending_claim_id, ep400_claim_id, JobState.PENDING.value, status_code=202)
    job_id = response_json['job']['job_id']

    await asyncio.sleep(ACCEPTABLE_JOB_PROCESSING_DURATION)

    response = await client.get(url=f"/merge/{job_id}")

    return response


class TestSuccess:

    @pytest.mark.asyncio(scope="session")
    @pytest.mark.parametrize(
        "pending_claim_id,ep400_claim_id",
        [
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DUPLICATE, id="with duplicate contention"),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DIFFERENT_CONTENTION_TYPE_CODE, id="with different contention type code"),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DIFFERENT_CLAIMANT_TEXT, id="with different claimant text"),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_MULTI_CONTENTION_ONE_DUPLICATE, id="with one duplicate, one not"),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES, id="with with no duplicates"),
        ],
    )
    async def test(self, pending_claim_id, ep400_claim_id):
        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client, pending_claim_id, ep400_claim_id)
            assert_response_and_job(response, pending_claim_id, ep400_claim_id, JobState.COMPLETED_SUCCESS)


class TestError:
    @pytest.mark.asyncio(scope="session")
    @pytest.mark.parametrize(
        "pending_claim_id,ep400_claim_id,expected_error_state,expected_num_errors",
        [
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS,
                EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES,
                JobState.GET_PENDING_CLAIM,
                1,
                id="fail to get pending claim details",
            ),
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CONTENTIONS,
                EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES,
                JobState.GET_PENDING_CLAIM_CONTENTIONS,
                1,
                id="fail to get pending claim contentions",
            ),
            pytest.param(
                PENDING_CLAIM_ID, CLAIM_ID_ERROR_AT_GET_CONTENTIONS, JobState.GET_EP400_CLAIM_CONTENTIONS, 1, id="fail to get ep400 claim contentions"
            ),
            pytest.param(PENDING_CLAIM_ID, CLAIM_ID_ERROR_AT_SET_TSOJ, JobState.SET_TEMP_STATION_OF_JURISDICTION, 1, id="fail to set tsoj on ep400"),
            pytest.param(
                CLAIM_ID_ERROR_AT_CREATE_CONTENTIONS,
                EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES,
                JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM,
                1,
                id="fail to move claim contentions to pending claim",
            ),
            pytest.param(PENDING_CLAIM_ID, CLAIM_ID_ERROR_AT_CANCEL_CLAIM, JobState.CANCEL_EP400_CLAIM, 1, id="fail to cancel ep400 claim"),
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS,
                CLAIM_ID_ERROR_AT_UPDATE_CONTENTIONS,
                JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE,
                2,
                id="fail to remove special issues from ep400 claim after failing to get pending claim",
            ),
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CONTENTIONS,
                CLAIM_ID_ERROR_AT_UPDATE_CONTENTIONS,
                JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE,
                2,
                id="fail to remove special issues from ep400 claim after failing to get pending claim contentions",
            ),
        ],
    )
    async def test(self, pending_claim_id, ep400_claim_id, expected_error_state, expected_num_errors):
        async with AsyncClient(app=app, base_url="http://test") as client:
            response = await submit_request_and_process(client, pending_claim_id, ep400_claim_id)
            assert_response_and_job(
                response,
                pending_claim_id=pending_claim_id,
                ep400_claim_id=ep400_claim_id,
                expected_state=JobState.COMPLETED_ERROR,
                expected_error_state=expected_error_state,
                expected_num_errors=expected_num_errors,
                status_code=200,
            )
