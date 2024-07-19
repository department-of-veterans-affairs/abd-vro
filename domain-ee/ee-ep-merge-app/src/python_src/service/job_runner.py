import asyncio
import logging

from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine, Workflow
from service.hoppy_service import HOPPY
from service.job_store import JOB_STORE

RESUME_IN_PROGRESS_JOBS_RETRY_RATE = 5


class JobRunner:
    def __init__(self) -> None:
        self.starting = True

    def is_ready(self) -> bool:
        return not self.starting

    async def start(self):
        """
        Resume any in-progress jobs that are in the database upon application startup, and after JOB_STORE and HOPPY
        are ready. If some are found, they are restarted or resumed depending on their current JobState.
        """
        while not JOB_STORE.is_ready() or not HOPPY.is_ready():
            logging.info("event=resumeJobsInProgress status=waitingForResources'")
            await asyncio.sleep(RESUME_IN_PROGRESS_JOBS_RETRY_RATE)

        jobs_to_resume = JOB_STORE.get_all_incomplete_jobs()
        self.starting = False

        logging.info(f'event=resumeJobsInProgress status=started total={len(jobs_to_resume)}')
        for index, in_progress_job in enumerate(jobs_to_resume):
            logging.info(f'event=resumeJobsInProgress status=running job={index+1} total={len(jobs_to_resume)}')
            await asyncio.get_event_loop().run_in_executor(None, self.resume_job, in_progress_job)
        logging.info(f'event=resumeJobsInProgress status=completed total={len(jobs_to_resume)}')

    @staticmethod
    def __start_machine(machine: EpMergeMachine) -> None:
        try:
            machine.start()
        except Exception as e:
            logging.error(f'event=jobInterrupted trigger={machine.main_event} job_id={machine.job.job_id} state={machine.job.state} error={e}')

    def start_job(self, merge_job: MergeJob) -> None:
        machine = EpMergeMachine(merge_job)
        self.__start_machine(machine)

    def resume_job(self, in_progress_job: MergeJob) -> None:
        if in_progress_job.state == JobState.CANCEL_EP400_CLAIM:
            machine = EpMergeMachine(in_progress_job, Workflow.RESUME_CANCEL_EP400)
        elif in_progress_job.state == JobState.ADD_CLAIM_NOTE_TO_EP400:
            machine = EpMergeMachine(in_progress_job, Workflow.RESUME_ADD_NOTE)
        else:
            machine = EpMergeMachine(in_progress_job, Workflow.RESTART)
        self.__start_machine(machine)


JOB_RUNNER = JobRunner()
