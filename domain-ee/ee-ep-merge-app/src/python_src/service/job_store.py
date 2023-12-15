from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import Session

from db.base_class import Base
from db.session import engine
from model.merge_job import MergeJob
from schema import merge_job as schema

Base.metadata.create_all(engine)


class JobStore:
    def init(self, db: Session):
        pass

    def clear(self, db: Session):
        db.query(MergeJob).delete()
        db.commit()

    def get_merge_jobs(self, db: Session) -> list[schema.MergeJob]:
        return db.query(MergeJob).all()

    def get_merge_job(self, job_id, db: Session) -> schema.MergeJob:
        return db.query(MergeJob).filter(MergeJob.job_id == job_id).first()

    def submit_merge_job(self, merge_job: schema.MergeJob, db: Session):
        job = MergeJob(**jsonable_encoder(dict(merge_job)))
        db.add(job)
        db.commit()

    def update_merge_job(self, merge_job: schema.MergeJob, db: Session):
        as_json = jsonable_encoder(dict(merge_job))
        db.query(MergeJob).filter(MergeJob.job_id == merge_job.job_id).update(as_json)
        db.commit()
