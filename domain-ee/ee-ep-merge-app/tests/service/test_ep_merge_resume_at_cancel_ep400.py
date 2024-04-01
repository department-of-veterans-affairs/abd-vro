from unittest.mock import call

import pytest
from conftest import (
    EP400_CLAIM_ID,
    JOB_ID,
    PENDING_CLAIM_ID,
    assert_hoppy_requests,
    add_claim_note_200,
    add_claim_note_req,
    cancel_claim_200,
    cancel_ep400_claim_req,
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
    revert_temporary_station_of_jurisdiction_200,
    revert_temporary_station_of_jurisdiction_req,
    update_contentions_on_ep400_200,
    update_contentions_on_ep400_req,
)
from hoppy.exception import ResponseException
from schema import (
    add_claim_note,
    cancel_claim,
    get_claim,
    get_contentions,
    update_contentions,
)
from schema import update_temp_station_of_jurisdiction as tsoj
from schema.claim import ClaimDetail
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine, Workflow


@pytest.fixture
def machine():
    return EpMergeMachine(
        MergeJob(job_id=JOB_ID, pending_claim_id=PENDING_CLAIM_ID, ep400_claim_id=EP400_CLAIM_ID, state=JobState.CANCEL_EP400_CLAIM),
        Workflow.RESUME_CANCEL_EP400,
    )


class TestUpToGetPendingClaim:
    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, get_claim.Response), id="400"),
            pytest.param(load_response(response_404, get_claim.Response), id="404"),
            pytest.param(load_response(response_500, get_claim.Response), id="500"),
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage="OK", claim=ClaimDetail(claimId=3, claimLifecycleStatus="Open")).model_dump(),
                id="claim has no endProductCode",
            ),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [invalid_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
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
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, but there are no ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
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
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, fails on call to get ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
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
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                # calls below are to attempt remove the special issue codes upon failure to get pending_claim, fails on update ep400 contentions
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )


class TestUpToCancelClaim:
    @pytest.mark.parametrize(
        "invalid_request,original_tsoj",
        [
            pytest.param(ResponseException("Oops"), "111", id="Caught Exception"),
            pytest.param(load_response(response_400, cancel_claim.Response), "111", id="400"),
            pytest.param(load_response(response_404, cancel_claim.Response), "111", id="404"),
            pytest.param(load_response(response_500, cancel_claim.Response), "111", id="500"),
            pytest.param(ResponseException("Oops"), None, id="Caught Exception, no original_tsoj"),
            pytest.param(load_response(response_400, cancel_claim.Response), None, id="400, no original_tsoj"),
            pytest.param(load_response(response_404, cancel_claim.Response), None, id="404, no original_tsoj"),
            pytest.param(load_response(response_500, cancel_claim.Response), None, id="500, no original_tsoj"),
        ],
    )
    def test_invalid_request_at_cancel_claim_due_to_exception(self, machine, mock_hoppy_async_client, invalid_request, original_tsoj):
        get_pending_claim_200.claim.temp_station_of_jurisdiction = original_tsoj
        revert_temporary_station_of_jurisdiction_req['tempStationOfJurisdiction'] = original_tsoj

        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, invalid_request, revert_temporary_station_of_jurisdiction_200])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CANCEL_EP400_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                # calls below are to attempt revert tsoj due to fail to cancel
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )

    @pytest.mark.parametrize(
        "invalid_request",
        [
            pytest.param(ResponseException("Oops"), id="Caught Exception"),
            pytest.param(load_response(response_400, tsoj.Response), id="400"),
            pytest.param(load_response(response_404, tsoj.Response), id="404"),
            pytest.param(load_response(response_500, tsoj.Response), id="500"),
        ],
    )
    def test_invalid_request_at_revert_tsoj_due_to_failure_to_cancel_claim(self, machine, mock_hoppy_async_client, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, ResponseException("Oops"), invalid_request])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                # calls below are to attempt revert tsoj due to fail to cancel which fails
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
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
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, cancel_claim_200, invalid_request])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.ADD_CLAIM_NOTE_TO_EP400, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                # call below is attempt to add note, which fails
                call(machine.job.job_id, add_claim_note_req),
            ],
        )


class TestSuccess:

    def test_process_succeeds_with_different_contention(self, machine, mock_hoppy_async_client):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, cancel_claim_200, add_claim_note_200])
        process_and_assert(machine, JobState.COMPLETED_SUCCESS)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [call(machine.job.job_id, get_pending_claim_req), call(machine.job.job_id, cancel_ep400_claim_req), call(machine.job.job_id, add_claim_note_req)],
        )
