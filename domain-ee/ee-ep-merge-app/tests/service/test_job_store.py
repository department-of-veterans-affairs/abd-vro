from model.merge_job import MergeJob
from src.python_src.service.job_store import JobStore


def test_init(db):
    job_store = JobStore(db)
    result = job_store.init()
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
    updated_merge_job = db.update.call_args[0][2]
    assert str(updated_merge_job.job_id) == str(merge_job.job_id)
    assert updated_merge_job.pending_claim_id == merge_job.pending_claim_id
    assert updated_merge_job.ep400_claim_id == merge_job.ep400_claim_id
    assert updated_merge_job.state == merge_job.state
    db.update.assert_called_once()
