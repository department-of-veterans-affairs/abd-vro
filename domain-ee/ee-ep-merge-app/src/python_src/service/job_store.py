from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import Session

from contextlib import contextmanager
from db.base_class import Base
from db.session import engine, SessionLocal
from model.merge_job import MergeJob
from schema import merge_job as schema

Base.metadata.create_all(engine)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


class JobStore:
    def with_connection(func):
        def wrapper(self, *args, **kwargs):
            with contextmanager(get_db)() as db:
                return func(self, *args, **kwargs, db=db)
        return wrapper

    def init(self) -> list[MergeJob]:
        jobs_in_progress = [schema.MergeJob.model_validate(job) for job in self.get_merge_jobs_in_progress()]
        jobs_to_restart = []
        for job in jobs_in_progress:
            if job.state == schema.JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM:
                job.error()
                self.update_merge_job(job)
            elif job.state == schema.JobState.RUNNING_CANCEL_EP400_CLAIM or job.state == schema.JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400:
                jobs_to_restart.append(job)
            elif job.state != schema.JobState.COMPLETED_ERROR:
                job.state = schema.JobState.PENDING
                self.update_merge_job(job)
                jobs_to_restart.append(job)
        return jobs_to_restart

    @with_connection
    def clear(self, db: Session):
        db.query(MergeJob).delete()
        db.commit()

    @with_connection
    def get_merge_jobs_in_progress(self, db: Session) -> list[MergeJob]:
        return db.query(MergeJob).filter(MergeJob.state != schema.JobState.COMPLETED_SUCCESS).all()

    @with_connection
    def get_merge_job(self, job_id, db: Session) -> MergeJob:
        return db.query(MergeJob).filter(MergeJob.job_id == job_id).first()

    @with_connection
    def submit_merge_job(self, merge_job: schema.MergeJob, db: Session):
        job = MergeJob(**jsonable_encoder(dict(merge_job)))
        db.add(job)
        db.commit()

    @with_connection
    def update_merge_job(self, merge_job: schema.MergeJob, db: Session):
        as_json = jsonable_encoder(dict(merge_job))
        db.query(MergeJob).filter(MergeJob.job_id == merge_job.job_id).update(as_json)
        db.commit()


job_store = JobStore()
