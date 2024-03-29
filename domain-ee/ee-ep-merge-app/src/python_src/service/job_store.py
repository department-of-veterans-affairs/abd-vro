from db.database import Database, database
from fastapi.encoders import jsonable_encoder
from model.merge_job import MergeJob
from schema import merge_job as schema


class JobStore:

    def __init__(self, db: Database):
        self.db = db

    def is_ready(self):
        return self.db.is_ready(schema.MergeJob)

    def get_all_incomplete_jobs(self) -> list[MergeJob]:
        return [schema.MergeJob.model_validate(job) for job in self.get_merge_jobs_in_progress()]

    def clear(self):
        self.db.clear(MergeJob)

    def query(self, states: list[schema.JobState] = schema.JobState.incomplete_states(), offset: int = 1, limit: int = 10) -> list[MergeJob]:
        return self.db.query(MergeJob, MergeJob.state.in_(states) if states else True, MergeJob.updated_at, offset, limit)

    def get_merge_jobs_in_progress(self) -> list[MergeJob]:
        return self.db.query_all(MergeJob, MergeJob.state.in_((schema.JobState.incomplete_states())))

    def get_merge_job(self, job_id) -> MergeJob:
        return self.db.query_first(MergeJob, MergeJob.job_id == job_id)

    def submit_merge_job(self, merge_job: schema.MergeJob):
        job = MergeJob(**jsonable_encoder(dict(merge_job)))
        self.db.add(job)

    def update_merge_job(self, merge_job: schema.MergeJob):
        self.db.update(merge_job)


JOB_STORE = JobStore(database)
