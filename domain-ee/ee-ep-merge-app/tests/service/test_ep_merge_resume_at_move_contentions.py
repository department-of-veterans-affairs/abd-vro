import pytest
from conftest import EP400_CLAIM_ID, JOB_ID, PENDING_CLAIM_ID, process_and_assert
from src.python_src.schema.merge_job import JobState, MergeJob
from src.python_src.service.ep_merge_machine import EpMergeMachine, Workflow


@pytest.fixture
def machine():
    return EpMergeMachine(MergeJob(job_id=JOB_ID,
                                   pending_claim_id=PENDING_CLAIM_ID,
                                   ep400_claim_id=EP400_CLAIM_ID,
                                   state=JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM),
                          Workflow.RESUME_MOVE_CONTENTIONS)


def test_resume_results_in_completed_error(mock_hoppy_async_client, machine):
    machine.job.error("Fail")
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM, 1)
    mock_hoppy_async_client.make_request.assert_not_called()
