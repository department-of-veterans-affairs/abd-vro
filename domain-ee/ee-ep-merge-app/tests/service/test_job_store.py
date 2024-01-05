import pytest
from model.merge_job import MergeJob
from schema.merge_job import JobState
from src.python_src.service.job_store import JobStore

DEFAULT_STATES = JobState.incomplete_states()
DEFAULT_OFFSET = 1
DEFAULT_LIMIT = 10


def test_reinitialize_in_progress_jobs(db):
    job_store = JobStore(db)
    result = job_store.reinitialize_in_progress_jobs()
    assert result == []


def test_clear(db):
    job_store = JobStore(db)
    job_store.clear()
    db.clear.assert_called_once_with(MergeJob)


def test_get_merge_jobs_in_progress(db):
    job_store = JobStore(db)
    job_store.get_merge_jobs_in_progress()
    assert db.query_all.call_args[0][0] == MergeJob


def test_get_merge_job(db):
    job_store = JobStore(db)
    job_id = 1
    job_store.get_merge_job(job_id)
    assert db.query_first.call_args[0][0] == MergeJob
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


@pytest.mark.parametrize("states,offset,limit",
                         [
                             pytest.param(None, None, None, id="defaults"),
                             pytest.param([JobState.PENDING], 1, None, id="first page, default 10 items"),
                             pytest.param([JobState.PENDING], None, 1, id="first page, default offset, 1 item"),
                             pytest.param([JobState.PENDING], 1, 10, id="last page, 1 item"),
                             pytest.param([JobState.PENDING], 1, 2, id="first page, 2 items"),
                             pytest.param([JobState.PENDING], 6, 2, id="last page, 2 items"),
                         ])
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
    actual_order_by = query_args[1]
    actual_filter = query_args[2]
    actual_offset = query_args[3]
    actual_limit = query_args[4]

    assert actual_model == MergeJob
    assert actual_order_by == MergeJob.updated_at
    assert actual_filter.compare(MergeJob.state.in_(states if states else DEFAULT_STATES))
    assert actual_offset == offset if offset else DEFAULT_OFFSET
    assert actual_limit == limit if limit else DEFAULT_LIMIT

    db.query.assert_called_once()
