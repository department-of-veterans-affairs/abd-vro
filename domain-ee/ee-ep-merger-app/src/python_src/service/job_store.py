from merge_job import MergeJob


class JobStore:
    _JOB_MAP = {}

    def clear(self):
        self._JOB_MAP.clear()

    def get_merge_jobs(self):
        return self._JOB_MAP.values()

    def get_merge_job(self, job_id):
        return self._JOB_MAP.get(str(job_id))

    def submit_merge_job(self, merge_job: MergeJob):
        self._JOB_MAP[str(merge_job.job_id)] = merge_job
