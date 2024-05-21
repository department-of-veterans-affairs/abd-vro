import asyncio
from datetime import datetime
from uuid import uuid4

import pytest
from src.python_src.schema.merge_job import JobState, MergeJob
from src.python_src.service.job_runner import JOB_RUNNER
from src.python_src.service.job_store import JOB_STORE

PENDING_CLAIM_ID = 10000
EP400_WITH_DUPLICATE = 10001
EP400_WITH_DIFFERENT_CONTENTION_TYPE_CODE = 10002
EP400_WITH_DIFFERENT_CLAIMANT_TEXT = 10003
EP400_WITH_MULTI_CONTENTION_ONE_DUPLICATE = 10004
EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES = 10005

CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS = 5001
CLAIM_ID_ERROR_AT_CANCEL_CLAIM = 5002
CLAIM_ID_ERROR_AT_UPDATE_CONTENTIONS = 5005

NOW = datetime.now()


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


class TestSuccess:
    @pytest.mark.asyncio(scope='session')
    @pytest.mark.parametrize(
        'pending_claim_id,ep400_claim_id',
        [
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DUPLICATE, id='with duplicate contention'),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DIFFERENT_CONTENTION_TYPE_CODE, id='with different contention type code'),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_DIFFERENT_CLAIMANT_TEXT, id='with different claimant text'),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_MULTI_CONTENTION_ONE_DUPLICATE, id='with one duplicate, one not'),
            pytest.param(PENDING_CLAIM_ID, EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES, id='with with no duplicates'),
        ],
    )
    async def test(self, pending_claim_id, ep400_claim_id):
        job_id = uuid4()
        job = MergeJob(
            job_id=job_id,
            pending_claim_id=pending_claim_id,
            ep400_claim_id=ep400_claim_id,
            state=JobState.CANCEL_EP400_CLAIM,
            created_at=NOW,
            updated_at=NOW,
        )
        JOB_STORE.submit_merge_job(job)

        await asyncio.get_event_loop().run_in_executor(None, JOB_RUNNER.resume_job, job)
        assert_job(job_id, pending_claim_id, ep400_claim_id, JobState.COMPLETED_SUCCESS)


class TestError:
    @pytest.mark.asyncio(scope='session')
    @pytest.mark.parametrize(
        'pending_claim_id,ep400_claim_id,expected_error_state,expected_num_errors',
        [
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS,
                EP400_WITH_MULTI_CONTENTION_NO_DUPLICATES,
                JobState.GET_PENDING_CLAIM,
                1,
                id='fail to get pending claim details',
            ),
            pytest.param(PENDING_CLAIM_ID, CLAIM_ID_ERROR_AT_CANCEL_CLAIM, JobState.CANCEL_EP400_CLAIM, 1, id='fail to cancel ep400 claim'),
            pytest.param(
                CLAIM_ID_ERROR_AT_GET_CLAIM_DETAILS,
                CLAIM_ID_ERROR_AT_UPDATE_CONTENTIONS,
                JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE,
                2,
                id='fail to remove special issues from ep400 claim after failing to get pending claim',
            ),
        ],
    )
    async def test(self, pending_claim_id, ep400_claim_id, expected_error_state, expected_num_errors):
        job_id = uuid4()
        job = MergeJob(
            job_id=job_id,
            pending_claim_id=pending_claim_id,
            ep400_claim_id=ep400_claim_id,
            state=JobState.CANCEL_EP400_CLAIM,
            created_at=NOW,
            updated_at=NOW,
        )
        JOB_STORE.submit_merge_job(job)

        await asyncio.get_event_loop().run_in_executor(None, JOB_RUNNER.resume_job, job)
        assert_job(job_id, pending_claim_id, ep400_claim_id, JobState.COMPLETED_ERROR, expected_error_state, expected_num_errors)
