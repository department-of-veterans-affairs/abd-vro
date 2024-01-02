from db.database import Database, database
from fastapi.encoders import jsonable_encoder
from model.merge_job import MergeJob
from schema import merge_job as schema


class JobStore:

    def __init__(self, db: Database):
        self.db = db

    def init(self) -> list[MergeJob]:
        jobs_in_progress = [schema.MergeJob.model_validate(job) for job in self.get_merge_jobs_in_progress()]
        jobs_to_restart = []
        for job in jobs_in_progress:
            if job.state == schema.JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM:
                job.error()
                self.update_merge_job(job)
            elif job.state == schema.JobState.RUNNING_CANCEL_EP400_CLAIM or job.state == schema.JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400:
                jobs_to_restart.append(job)
            else:
                job.state = schema.JobState.PENDING
                self.update_merge_job(job)
                jobs_to_restart.append(job)
        return jobs_to_restart

    def clear(self):
        self.db.clear(MergeJob)

    def query(self, states: list[schema.JobState] = schema.JobState.incomplete(), offset: int = 1, limit: int = 10) -> \
            list[MergeJob]:
        return self.db.query(MergeJob,
                             MergeJob.updated_at,
                             MergeJob.state.in_(states) if states else True,
                             offset,
                             limit)

    def get_merge_jobs_in_progress(self) -> list[MergeJob]:
        return self.db.query_all(MergeJob, MergeJob.state.in_((schema.JobState.incomplete())))

    def get_merge_job(self, job_id) -> MergeJob:
        return self.db.query_first(MergeJob, MergeJob.job_id == job_id)

    def submit_merge_job(self, merge_job: schema.MergeJob):
        job = MergeJob(**jsonable_encoder(dict(merge_job)))
        self.db.add(job)

    def update_merge_job(self, merge_job: schema.MergeJob):
        self.db.update(merge_job)


job_store = JobStore(database)
