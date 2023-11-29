from typing import Callable
from pydantic_models import MergeJob
from apscheduler.schedulers.asyncio import AsyncIOScheduler


class JobStore:
    def __init__(self, scheduler: AsyncIOScheduler, func: Callable):
        self.scheduler = scheduler
        self.func = func

    def clear(self):
        self.scheduler.remove_all_jobs()

    def get_merge_jobs(self) -> list[MergeJob]:
        return [job.args[0] for job in self.scheduler.get_jobs() if len(job.args) > 0]

    def get_merge_job(self, job_id) -> MergeJob:
        try:
            return self.scheduler.get_job(str(job_id)).args[0]
        except (AttributeError, IndexError):
            return None

    def submit_merge_job(self, merge_job: MergeJob):
        self.scheduler.add_job(self.func, args=[merge_job], id=str(merge_job.job_id), replace_existing=True)
