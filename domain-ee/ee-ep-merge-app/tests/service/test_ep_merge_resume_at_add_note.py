from unittest.mock import call

import pytest
from conftest import (
    EP400_CLAIM_ID,
    JOB_ID,
    PENDING_CLAIM_ID,
    add_claim_note_200,
    add_claim_note_req,
    get_ep400_contentions_200,
    get_ep400_contentions_req,
    get_pending_claim_200,
    get_pending_claim_req,
    load_response,
    mock_async_responses,
    process_and_assert,
    response_400,
    response_404,
    response_500,
    update_contentions_on_ep400_200,
    update_contentions_on_ep400_req,
)
from hoppy.exception import ResponseException
from schema import add_claim_note, get_claim, get_contentions, update_contentions
from schema.claim import ClaimDetail
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine, Workflow


@pytest.fixture
def machine():
    return EpMergeMachine(
        MergeJob(job_id=JOB_ID, pending_claim_id=PENDING_CLAIM_ID, ep400_claim_id=EP400_CLAIM_ID, state=JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400),
        Workflow.RESUME_ADD_NOTE,
    )


class TestUpToGetPendingClaim:
    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, get_claim.Response), id="400"),
            pytest.param(load_response(response_404, get_claim.Response), id="404"),
            pytest.param(load_response(response_500, get_claim.Response), id="500"),
            pytest.param(get_claim.Response(statusCode=200, statusMessage="OK", claim=ClaimDetail(claimId=3)).model_dump(), id="claim has no endProductCode"),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [invalid_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM, 1)
        mock_hoppy_async_client.make_request.assert_has_calls(
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ]
        )

    @pytest.mark.parametrize(
        "no_contentions_response",
        [
            pytest.param(get_contentions.Response(status_code=200, status_message="OK"), id="Implicit None"),
            pytest.param(get_contentions.Response(status_code=200, status_message="OK", contentions=None), id="Explicit None"),
            pytest.param(get_contentions.Response(status_code=200, status_message="OK", contentions=[]), id="Empty"),
        ],
    )
    def test_no_contentions_on_ep400_after_get_pending_claim_failure(self, machine, mock_hoppy_async_client, no_contentions_response):
        mock_async_responses(mock_hoppy_async_client, [ResponseException("Oops"), no_contentions_response])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM, 1)
        mock_hoppy_async_client.make_request.assert_has_calls(
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, but there are no ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
            ]
        )

    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, get_contentions.Response), id="400"),
            pytest.param(load_response(response_404, get_contentions.Response), id="404"),
            pytest.param(load_response(response_500, get_contentions.Response), id="500"),
        ],
    )
    def test_invalid_request_at_get_ep400_contentions_after_get_pending_claim_failure(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                ResponseException("Oops"),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        mock_hoppy_async_client.make_request.assert_has_calls(
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, fails on call to get ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
            ]
        )

    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, update_contentions.Response), id="400"),
            pytest.param(load_response(response_404, update_contentions.Response), id="404"),
            pytest.param(load_response(response_500, update_contentions.Response), id="500"),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_pending_claim_failure(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                ResponseException("Oops"),
                get_ep400_contentions_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        mock_hoppy_async_client.make_request.assert_has_calls(
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, fails on update ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
            ]
        )


class TestUpToAddClaimNote:

    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, add_claim_note.Response), id="400"),
            pytest.param(load_response(response_500, add_claim_note.Response), id="500"),
        ],
    )
    def test_invalid_request_at_add_claim_note_due_to_exception(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, invalid_request])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400, 1)
        mock_hoppy_async_client.make_request.assert_has_calls(
            [
                call(machine.job.job_id, get_pending_claim_req),
                # call below is attempt to add note, which fails
                call(machine.job.job_id, add_claim_note_req),
            ]
        )


class TestSuccess:

    def test_process_succeeds_with_different_contention(self, machine, mock_hoppy_async_client):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, add_claim_note_200])
        process_and_assert(machine, JobState.COMPLETED_SUCCESS)
        mock_hoppy_async_client.make_request.assert_has_calls([call(machine.job.job_id, get_pending_claim_req), call(machine.job.job_id, add_claim_note_req)])
