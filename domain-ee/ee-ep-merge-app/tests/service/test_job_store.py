from sqlalchemy.orm import Session
from unittest.mock import MagicMock

import pytest
import uuid

from src.python_src.service.job_store import JobStore
from src.python_src.schema import merge_job as schema
from model.merge_job import MergeJob


@pytest.fixture
def db():
    return MagicMock(spec=Session)


@pytest.fixture
def merge_job():
    return schema.MergeJob(job_id=uuid.uuid4(), pending_claim_id=1, ep400_claim_id=2, state="PENDING")


def test_init(db):
    job_store = JobStore()
    result = job_store.init(db)
    assert result == []


def test_clear(db):
    job_store = JobStore()
    job_store.clear(db)
    db.query.assert_called_once_with(MergeJob)
    db.query().delete.assert_called_once()


def test_get_merge_jobs(db):
    job_store = JobStore()
    result = job_store.get_merge_jobs(db)
    db.query.assert_called_once_with(MergeJob)
    assert result == db.query().all()


def test_get_merge_job(db):
    job_store = JobStore()
    job_id = 1
    result = job_store.get_merge_job(job_id, db)
    db.query.assert_called_once_with(MergeJob)
    assert db.query().filter.call_args[0][0].right.value == job_id
    assert result == db.query().filter().first()


def test_submit_merge_job(db, merge_job):
    job_store = JobStore()
    job_store.submit_merge_job(merge_job, db)
    added_merge_job = db.add.call_args[0][0]
    assert added_merge_job.job_id == str(merge_job.job_id)
    assert added_merge_job.pending_claim_id == merge_job.pending_claim_id
    assert added_merge_job.ep400_claim_id == merge_job.ep400_claim_id
    assert added_merge_job.state == merge_job.state
    db.commit.assert_called_once()


def test_update_merge_job(db, merge_job):
    job_store = JobStore()
    job_store.update_merge_job(merge_job, db)
    db.query.assert_called_once_with(MergeJob)
    assert db.query().filter.call_args[0][0].right.value == merge_job.job_id
    db.query().filter().update.assert_called_once_with(merge_job.model_dump(mode='json'))
    db.commit.assert_called_once()
