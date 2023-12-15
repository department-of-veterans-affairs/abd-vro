from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import Session

from db.base_class import Base
from db.session import engine
from model.merge_job import MergeJob
from schema import merge_job as schema

Base.metadata.create_all(engine)


class JobStore:
    def init(self, db: Session) -> list[MergeJob]:
        jobs_in_progress = [schema.MergeJob.model_validate(job) for job in self.get_merge_jobs(db)]
        jobs_to_restart = []
        for job in jobs_in_progress:
            if job.state == schema.JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM:
                job.error()
                self.update_merge_job(job, db)
            elif job.state == schema.JobState.RUNNING_CANCEL_EP400_CLAIM or job.state == schema.JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400:
                jobs_to_restart.append(job)
            elif job.state != schema.JobState.COMPLETED_ERROR:
                job.state = schema.JobState.PENDING
                self.update_merge_job(job, db)
                jobs_to_restart.append(job)
        return jobs_to_restart

    def clear(self, db: Session):
        db.query(MergeJob).delete()
        db.commit()

    def get_merge_jobs(self, db: Session) -> list[MergeJob]:
        return db.query(MergeJob).all()

    def get_merge_job(self, job_id, db: Session) -> MergeJob:
        return db.query(MergeJob).filter(MergeJob.job_id == job_id).first()

    def submit_merge_job(self, merge_job: schema.MergeJob, db: Session):
        job = MergeJob(**jsonable_encoder(dict(merge_job)))
        db.add(job)
        db.commit()

    def update_merge_job(self, merge_job: schema.MergeJob, db: Session):
        if merge_job.state == schema.JobState.COMPLETED_SUCCESS:
            db.query(MergeJob).filter(MergeJob.job_id == merge_job.job_id).delete()
            db.commit()
            return
        as_json = jsonable_encoder(dict(merge_job))
        db.query(MergeJob).filter(MergeJob.job_id == merge_job.job_id).update(as_json)
        db.commit()


job_store = JobStore()
