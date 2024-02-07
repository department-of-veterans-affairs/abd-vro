from datetime import datetime
from uuid import uuid4

import model
import pytest
from schema.merge_job import JobState, MergeJob
from service.job_store import JobStore

DEFAULT_STATES = JobState.incomplete_states()
DEFAULT_OFFSET = 1
DEFAULT_LIMIT = 10


def create_job(state: JobState):
    return {
        'job_id': uuid4(),
        'pending_claim_id': 1,
        'ep400_claim_id': 2,
        'state': state.value,
        'created_at': datetime.now().isoformat(),
        'updated_at': datetime.now().isoformat(),
    }


@pytest.mark.parametrize(
    "in_progress_jobs",
    [
        pytest.param([], id="0 incomplete jobs"),
        pytest.param([create_job(JobState.PENDING)], id="1 incomplete jobs"),
        pytest.param([create_job(JobState.PENDING), create_job(JobState.CANCEL_EP400_CLAIM)], id="2 incomplete jobs"),
        pytest.param([create_job(state) for state in JobState.incomplete_states()], id="1 job in every incomplete state"),
    ],
)
def test_get_all_incomplete_jobs(db, in_progress_jobs):
    db.query_all.return_value = in_progress_jobs

    job_store = JobStore(db)
    result = job_store.get_all_incomplete_jobs()
    assert len(result) == len(in_progress_jobs)
    assert all(isinstance(job, MergeJob) for job in result)


def test_clear(db):
    job_store = JobStore(db)
    job_store.clear()
    db.clear.assert_called_once_with(model.merge_job.MergeJob)


def test_get_merge_jobs_in_progress(db):
    job_store = JobStore(db)
    job_store.get_merge_jobs_in_progress()
    assert db.query_all.call_args[0][0] == model.merge_job.MergeJob


def test_get_merge_job(db):
    job_store = JobStore(db)
    job_id = 1
    job_store.get_merge_job(job_id)
    assert db.query_first.call_args[0][0] == model.merge_job.MergeJob
    assert db.query_first.call_args[0][1].right.value == job_id


def test_submit_merge_job(db, merge_job):
    job_store = JobStore(db)
    job_store.submit_merge_job(merge_job)
    added_merge_job = db.add.call_args[0][0]
    assert added_merge_job.job_id == str(merge_job.job_id)
    assert added_merge_job.pending_claim_id == merge_job.pending_claim_id
    assert added_merge_job.ep400_claim_id == merge_job.ep400_claim_id
    assert added_merge_job.state == merge_job.state
    db.add.assert_called_once()


def test_update_merge_job(db, merge_job):
    job_store = JobStore(db)
    job_store.update_merge_job(merge_job)
    updated_merge_job = db.update.call_args[0][0]
    assert str(updated_merge_job.job_id) == str(merge_job.job_id)
    assert updated_merge_job.pending_claim_id == merge_job.pending_claim_id
    assert updated_merge_job.ep400_claim_id == merge_job.ep400_claim_id
    assert updated_merge_job.state == merge_job.state
    db.update.assert_called_once()


@pytest.mark.parametrize(
    "states,offset,limit",
    [
        pytest.param(None, None, None, id="defaults"),
        pytest.param([JobState.PENDING], 1, None, id="first page, default 10 items"),
        pytest.param([JobState.PENDING], None, 1, id="first page, default offset, 1 item"),
        pytest.param([JobState.PENDING], 1, 10, id="last page, 1 item"),
        pytest.param([JobState.PENDING], 1, 2, id="first page, 2 items"),
        pytest.param([JobState.PENDING], 6, 2, id="last page, 2 items"),
    ],
)
def test_query(db, merge_job, states: list, offset: int, limit: int):
    job_store = JobStore(db)
    kwargs = {}
    if states:
        kwargs["states"] = states
    if offset:
        kwargs['offset'] = offset
    if limit:
        kwargs['limit'] = limit

    job_store.query(**kwargs)

    query_args = db.query.call_args[0]
    actual_model = query_args[0]
    actual_filter = query_args[1]
    actual_order_by = query_args[2]
    actual_offset = query_args[3]
    actual_limit = query_args[4]

    assert actual_model == model.merge_job.MergeJob
    assert actual_filter.compare(model.merge_job.MergeJob.state.in_(states if states else DEFAULT_STATES))
    assert actual_order_by == model.merge_job.MergeJob.updated_at
    assert actual_offset == offset if offset else DEFAULT_OFFSET
    assert actual_limit == limit if limit else DEFAULT_LIMIT

    db.query.assert_called_once()
