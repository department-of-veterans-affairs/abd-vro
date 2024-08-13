from unittest.mock import Mock, call

import pytest
from conftest import (
    EP400_CLAIM_ID,
    JOB_ID,
    PENDING_CLAIM_ID,
    add_claim_note_200,
    add_claim_note_req,
    assert_hoppy_requests,
    assert_metrics_called,
    assert_requests_and_metrics_for_success,
    cancel_claim_200,
    cancel_ep400_claim_req,
    create_contentions_on_pending_claim_201,
    create_contentions_on_pending_claim_req,
    ep400_contentions_increase_multicontention_200,
    ep400_contentions_increase_tinnitus_200,
    ep400_contentions_new_tinnitus_200,
    get_ep400_claim_200,
    get_ep400_claim_req,
    get_ep400_contentions_200,
    get_ep400_contentions_204,
    get_ep400_contentions_req,
    get_pending_claim_200,
    get_pending_claim_req,
    get_pending_contentions_200,
    get_pending_contentions_increase_tinnitus_200,
    get_pending_contentions_req,
    load_response,
    mock_async_responses,
    pending_contentions_increase_tendinitis_200,
    pending_contentions_increase_tinnitus_200,
    process_and_assert,
    response_400,
    response_404,
    response_500,
    revert_temporary_station_of_jurisdiction_200,
    revert_temporary_station_of_jurisdiction_req,
    update_contentions_on_ep400_200,
    update_contentions_on_ep400_req,
    update_temporary_station_of_jurisdiction_200,
    update_temporary_station_of_jurisdiction_req,
)
from hoppy.exception import ResponseException
from schema import (
    add_claim_note,
    cancel_claim,
    create_contentions,
    get_claim,
    get_contentions,
    update_contentions,
)
from schema import update_temp_station_of_jurisdiction as tsoj
from schema.claim import BenefitClaimType, ClaimDetail
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import EpMergeMachine


@pytest.fixture
def machine():
    return EpMergeMachine(MergeJob(job_id=JOB_ID, pending_claim_id=PENDING_CLAIM_ID, ep400_claim_id=EP400_CLAIM_ID))


def test_constructor():
    merge_job = Mock()
    machine = EpMergeMachine(merge_job)

    assert machine.current_state_value == JobState.PENDING


class TestUpToGetPendingClaim:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_claim.Response), id='400'),
            pytest.param(load_response(response_404, get_claim.Response), id='404'),
            pytest.param(load_response(response_500, get_claim.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [invalid_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM)

    @pytest.mark.parametrize(
        'ineligible_request',
        [
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, claimLifecycleStatus='OPEN')).model_dump(),
                id='claim has no endProductCode',
            ),
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010')).model_dump(),
                id='claim has no claim_lifecycle_status',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Cancelled')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Cancelled'",
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Ready For Decision')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Ready to Work'",
            ),
        ],
    )
    def test_ineligible_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, ineligible_request):
        mock_async_responses(mock_hoppy_async_client, [ineligible_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.ABORTED, JobState.GET_PENDING_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.ABORTED, JobState.GET_PENDING_CLAIM)

    @pytest.mark.parametrize(
        'no_contentions_response',
        [
            pytest.param(get_contentions.Response(status_code=200, status_message='OK'), id='Implicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=None), id='Explicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=[]), id='Empty'),
        ],
    )
    def test_no_contentions_on_ep400_after_get_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, no_contentions_response
    ):
        mock_async_responses(mock_hoppy_async_client, [ResponseException('Oops'), no_contentions_response])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_contentions.Response), id='400'),
            pytest.param(load_response(response_404, get_contentions.Response), id='404'),
            pytest.param(load_response(response_500, get_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_get_ep400_contentions_after_get_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE
        )

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                ResponseException('Oops'),
                get_ep400_contentions_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE
        )


class TestUpToGetEP400Claim:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_claim.Response), id='400'),
            pytest.param(load_response(response_404, get_claim.Response), id='404'),
            pytest.param(load_response(response_500, get_claim.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, invalid_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM)

    @pytest.mark.parametrize(
        'ineligible_request',
        [
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3)).model_dump(), id='claim does not have lifecycle status'
            ),
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, claimLifecycleStatus='Closed')).model_dump(),
                id='claim is not open',
            ),
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, claimLifecycleStatus='Open')).model_dump(),
                id='claim has no endProductCode',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='399', claimLifecycleStatus='Open')
                ).model_dump(),
                id='claim endProductCode not supported',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='400', claimLifecycleStatus='Open')
                ).model_dump(),
                id='claim no benefitClaimType',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200,
                    statusMessage='OK',
                    claim=ClaimDetail(claimId=3, endProductCode='400', benefitClaimType=BenefitClaimType(), claimLifecycleStatus='Open'),
                ).model_dump(),
                id='claim no benefitClaimType.code',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200,
                    statusMessage='OK',
                    claim=ClaimDetail(claimId=3, endProductCode='400', benefitClaimType=BenefitClaimType(code='NOTTHISCODE'), claimLifecycleStatus='Open'),
                ).model_dump(),
                id='claim benefitClaimType.code not supported',
            ),
        ],
    )
    def test_ineligible_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, ineligible_request):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, ineligible_request, get_ep400_contentions_200, update_contentions_on_ep400_200])
        process_and_assert(machine, JobState.ABORTED, JobState.GET_EP400_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.ABORTED, JobState.GET_EP400_CLAIM)

    @pytest.mark.parametrize(
        'no_contentions_response',
        [
            pytest.param(get_contentions.Response(status_code=200, status_message='OK'), id='Implicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=None), id='Explicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=[]), id='Empty'),
        ],
    )
    def test_no_contentions_on_ep400_after_get_ep400_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, no_contentions_response
    ):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, ResponseException('Oops'), no_contentions_response])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_contentions.Response), id='400'),
            pytest.param(load_response(response_404, get_contentions.Response), id='404'),
            pytest.param(load_response(response_500, get_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_get_ep400_contentions_after_get_ep400_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE
        )

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_ep400_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                ResponseException('Oops'),
                get_ep400_contentions_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_FAILED_REMOVE_SPECIAL_ISSUE
        )


class TestUpToGetPendingContentions:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_contentions.Response), id='400'),
            pytest.param(load_response(response_404, get_contentions.Response), id='404'),
            pytest.param(load_response(response_500, get_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client, [get_pending_claim_200, get_ep400_claim_200, invalid_request, get_ep400_contentions_200, update_contentions_on_ep400_200]
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS)

    @pytest.mark.parametrize(
        'no_contentions_response',
        [
            pytest.param(get_contentions.Response(status_code=200, status_message='OK'), id='Implicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=None), id='Explicit None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=[]), id='Empty'),
        ],
    )
    def test_no_contentions_on_ep400_after_get_pending_contentions_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, no_contentions_response
    ):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, get_ep400_claim_200, ResponseException('Oops'), no_contentions_response])
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_contentions.Response), id='400'),
            pytest.param(load_response(response_404, get_contentions.Response), id='404'),
            pytest.param(load_response(response_500, get_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_get_ep400_contentions_after_get_pending_contentions_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE,
        )

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_pending_contentions_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                ResponseException('Oops'),
                get_ep400_contentions_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.GET_PENDING_CLAIM_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE,
        )


class TestUpToGetEp400Contentions:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_contentions.Response), id='400'),
            pytest.param(load_response(response_404, get_contentions.Response), id='404'),
            pytest.param(load_response(response_500, get_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_get_ep400_contentions(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client, [get_pending_claim_200, get_ep400_claim_200, get_pending_contentions_200, invalid_request, invalid_request]
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_CONTENTIONS, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.GET_EP400_CLAIM_CONTENTIONS)

    @pytest.mark.parametrize(
        'valid_request',
        [
            pytest.param(get_ep400_contentions_204, id='No contentions found'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=None), id='None'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=[]), id='Empty'),
        ],
    )
    def test_valid_request_but_empty_contentions_at_get_ep400_contentions(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, valid_request
    ):
        mock_async_responses(mock_hoppy_async_client, [get_pending_claim_200, get_ep400_claim_200, get_pending_contentions_200, valid_request, valid_request])
        process_and_assert(machine, JobState.ABORTED, JobState.GET_EP400_CLAIM_CONTENTIONS, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.ABORTED, JobState.GET_EP400_CLAIM_CONTENTIONS)


class TestUpToCheckPendingIsOpen:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_claim.Response), id='400'),
            pytest.param(load_response(response_404, get_claim.Response), id='404'),
            pytest.param(load_response(response_500, get_claim.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                invalid_request,
                update_contentions_on_ep400_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CHECK_PENDING_EP_IS_OPEN, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.CHECK_PENDING_EP_IS_OPEN)

    @pytest.mark.parametrize(
        'ineligible_request',
        [
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010')).model_dump(),
                id='claim has no claim_lifecycle_status',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Cancelled')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Cancelled'",
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Ready For Decision')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Ready to Work'",
            ),
        ],
    )
    def test_ineligible_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, ineligible_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                ineligible_request,
                update_contentions_on_ep400_200,
            ],
        )
        process_and_assert(machine, JobState.ABORTED, JobState.CHECK_PENDING_EP_IS_OPEN, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.ABORTED, JobState.CHECK_PENDING_EP_IS_OPEN)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CHECK_PENDING_EP_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.CHECK_PENDING_EP_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE
        )


class TestUpToCheckEP400IsOpen:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, get_claim.Response), id='400'),
            pytest.param(load_response(response_404, get_claim.Response), id='404'),
            pytest.param(load_response(response_500, get_claim.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                invalid_request,
                update_contentions_on_ep400_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CHECK_EP400_IS_OPEN, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.CHECK_EP400_IS_OPEN)

    @pytest.mark.parametrize(
        'ineligible_request',
        [
            pytest.param(
                get_claim.Response(statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010')).model_dump(),
                id='claim has no claim_lifecycle_status',
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Cancelled')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Cancelled'",
            ),
            pytest.param(
                get_claim.Response(
                    statusCode=200, statusMessage='OK', claim=ClaimDetail(claimId=3, endProductCode='010', claimLifecycleStatus='Ready For Decision')
                ).model_dump(),
                id="claim has ineligible claim_lifecycle_status 'Ready to Work'",
            ),
        ],
    )
    def test_ineligible_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, ineligible_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                ineligible_request,
                update_contentions_on_ep400_200,
            ],
        )
        process_and_assert(machine, JobState.ABORTED, JobState.CHECK_EP400_IS_OPEN, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.ABORTED, JobState.CHECK_EP400_IS_OPEN)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_at_update_ep400_contentions_after_get_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CHECK_EP400_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.CHECK_EP400_IS_OPEN_FAILED_REMOVE_SPECIAL_ISSUE
        )


class TestUpToSetTemporaryStationOfJurisdiction:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, tsoj.Response), id='400'),
            pytest.param(load_response(response_404, tsoj.Response), id='404'),
            pytest.param(load_response(response_500, tsoj.Response), id='500'),
        ],
    )
    def test_invalid_request(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                invalid_request,
                update_contentions_on_ep400_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.SET_TEMP_STATION_OF_JURISDICTION, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.SET_TEMP_STATION_OF_JURISDICTION)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_invalid_request_on_update_contentions_after_set_tsoj_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.SET_TEMP_STATION_OF_JURISDICTION_FAILED_REMOVE_SPECIAL_ISSUE,
        )


class TestUpToMoveContentionsToPendingClaim:
    @pytest.mark.parametrize(
        'invalid_request,original_tsoj',
        [
            pytest.param(ResponseException('Oops'), '111', id='Caught Exception'),
            pytest.param(load_response(response_400, create_contentions.Response), '111', id='400'),
            pytest.param(load_response(response_404, create_contentions.Response), '111', id='404'),
            pytest.param(load_response(response_500, create_contentions.Response), '111', id='500'),
            pytest.param(ResponseException('Oops'), None, id='Caught Exception, no original_tsoj'),
            pytest.param(load_response(response_400, create_contentions.Response), None, id='400, no original_tsoj'),
            pytest.param(load_response(response_404, create_contentions.Response), None, id='404, no original_tsoj'),
            pytest.param(load_response(response_500, create_contentions.Response), None, id='500, no original_tsoj'),
        ],
    )
    def test_fail(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request, original_tsoj):
        get_ep400_claim_200.claim.temp_station_of_jurisdiction = original_tsoj
        revert_temporary_station_of_jurisdiction_req['tempStationOfJurisdiction'] = original_tsoj

        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                invalid_request,
                update_contentions_on_ep400_200,
                revert_temporary_station_of_jurisdiction_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )
        assert_metrics_called(metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.MOVE_CONTENTIONS_TO_PENDING_CLAIM)

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, update_contentions.Response), id='400'),
            pytest.param(load_response(response_404, update_contentions.Response), id='404'),
            pytest.param(load_response(response_500, update_contentions.Response), id='500'),
        ],
    )
    def test_fail_to_remove_special_issues_after_move_contentions_to_pending_claim_failure(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                ResponseException('Oops'),
                invalid_request,
                revert_temporary_station_of_jurisdiction_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE, 2)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution, metric_logger_increment, JobState.COMPLETED_ERROR, JobState.MOVE_CONTENTIONS_FAILED_REMOVE_SPECIAL_ISSUE
        )

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, tsoj.Response), id='400'),
            pytest.param(load_response(response_404, tsoj.Response), id='404'),
            pytest.param(load_response(response_500, tsoj.Response), id='500'),
        ],
    )
    def test_fail_to_revert_tsoj_after_failure_to_move_contentions_to_pending_claim(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                ResponseException('Oops'),
                update_contentions_on_ep400_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, update_contentions_on_ep400_req),
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.MOVE_CONTENTIONS_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION,
        )


class TestUpToCancelClaim:
    @pytest.mark.parametrize(
        'invalid_request,original_tsoj',
        [
            pytest.param(ResponseException('Oops'), '111', id='Caught Exception'),
            pytest.param(load_response(response_400, cancel_claim.Response), '111', id='400'),
            pytest.param(load_response(response_404, cancel_claim.Response), '111', id='404'),
            pytest.param(load_response(response_500, cancel_claim.Response), '111', id='500'),
            pytest.param(ResponseException('Oops'), None, id='Caught Exception, no original_tsoj'),
            pytest.param(load_response(response_400, cancel_claim.Response), None, id='400, no original_tsoj'),
            pytest.param(load_response(response_404, cancel_claim.Response), None, id='404, no original_tsoj'),
            pytest.param(load_response(response_500, cancel_claim.Response), None, id='500, no original_tsoj'),
        ],
    )
    def test_invalid_request_at_cancel_claim_due_to_exception(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request, original_tsoj
    ):
        get_ep400_claim_200.claim.temp_station_of_jurisdiction = original_tsoj
        revert_temporary_station_of_jurisdiction_req['tempStationOfJurisdiction'] = original_tsoj

        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                create_contentions_on_pending_claim_201,
                invalid_request,
                revert_temporary_station_of_jurisdiction_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CANCEL_EP400_CLAIM, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.CANCEL_EP400_CLAIM,
            expected_pending_new_contentions=0,
            expected_pending_cfi_contentions=1,
            expected_ep400_new_contentions=0,
            expected_ep400_cfi_contentions=1,
            expected_merge_contentions=1,
            expected_merge_new_contentions=0,
            expected_merge_cfi_contentions=1,
            expected_merge_skip=False,
        )

    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, tsoj.Response), id='400'),
            pytest.param(load_response(response_404, tsoj.Response), id='404'),
            pytest.param(load_response(response_500, tsoj.Response), id='500'),
        ],
    )
    def test_invalid_request_at_revert_tsoj_due_to_failure_to_cancel_claim(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                create_contentions_on_pending_claim_201,
                ResponseException('Oops'),
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION, 3)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                call(machine.job.job_id, revert_temporary_station_of_jurisdiction_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.CANCEL_CLAIM_FAILED_REVERT_TEMP_STATION_OF_JURISDICTION,
            expected_pending_new_contentions=0,
            expected_pending_cfi_contentions=1,
            expected_ep400_new_contentions=0,
            expected_ep400_cfi_contentions=1,
            expected_merge_contentions=1,
            expected_merge_new_contentions=0,
            expected_merge_cfi_contentions=1,
            expected_merge_skip=False,
        )


class TestUpToAddClaimNote:
    @pytest.mark.parametrize(
        'invalid_request',
        [
            pytest.param(ResponseException('Oops'), id='Caught Exception'),
            pytest.param(load_response(response_400, add_claim_note.Response), id='400'),
            pytest.param(load_response(response_500, add_claim_note.Response), id='500'),
        ],
    )
    def test_invalid_request_at_add_claim_note_due_to_exception(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, invalid_request
    ):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                create_contentions_on_pending_claim_201,
                cancel_claim_200,
                invalid_request,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.ADD_CLAIM_NOTE_TO_EP400, 1)
        assert_hoppy_requests(
            mock_hoppy_async_client,
            [
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, get_pending_contentions_req),
                call(machine.job.job_id, get_ep400_contentions_req),
                call(machine.job.job_id, get_pending_claim_req),
                call(machine.job.job_id, get_ep400_claim_req),
                call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
                call(machine.job.job_id, create_contentions_on_pending_claim_req),
                call(machine.job.job_id, cancel_ep400_claim_req),
                call(machine.job.job_id, add_claim_note_req),
            ],
        )
        assert_metrics_called(
            metric_logger_distribution,
            metric_logger_increment,
            JobState.COMPLETED_ERROR,
            JobState.ADD_CLAIM_NOTE_TO_EP400,
            expected_pending_new_contentions=0,
            expected_pending_cfi_contentions=1,
            expected_ep400_new_contentions=0,
            expected_ep400_cfi_contentions=1,
            expected_merge_contentions=1,
            expected_merge_new_contentions=0,
            expected_merge_cfi_contentions=1,
            expected_merge_skip=False,
        )


class TestSuccess:
    @pytest.mark.parametrize(
        'get_contentions_res',
        [
            pytest.param(
                (
                    load_response(pending_contentions_increase_tendinitis_200, get_contentions.Response),
                    load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response),
                ),
                id='different contention name',
            ),
            pytest.param(
                (
                    load_response(pending_contentions_increase_tinnitus_200, get_contentions.Response),
                    load_response(ep400_contentions_new_tinnitus_200, get_contentions.Response),
                ),
                id='different contention type',
            ),
            pytest.param(
                (
                    load_response(pending_contentions_increase_tinnitus_200, get_contentions.Response),
                    load_response(ep400_contentions_increase_multicontention_200, get_contentions.Response),
                ),
                id='different contention name alongside duplicate',
            ),
        ],
    )
    def test_process_succeeds_with_different_contention(
        self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment, get_contentions_res
    ):
        pending_contentions_response, ep400_contentions_response = get_contentions_res
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                pending_contentions_response,
                ep400_contentions_response,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                create_contentions_on_pending_claim_201,
                cancel_claim_200,
                add_claim_note_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_SUCCESS)
        assert_requests_and_metrics_for_success(
            machine, metric_logger_distribution, metric_logger_increment, mock_hoppy_async_client, pending_contentions_response, ep400_contentions_response
        )

    def test_process_succeeds_with_duplicate_contention(self, machine, mock_hoppy_async_client, metric_logger_distribution, metric_logger_increment):
        mock_async_responses(
            mock_hoppy_async_client,
            [
                get_pending_claim_200,
                get_ep400_claim_200,
                get_pending_contentions_increase_tinnitus_200,
                get_ep400_contentions_200,
                get_pending_claim_200,
                get_ep400_claim_200,
                update_temporary_station_of_jurisdiction_200,
                cancel_claim_200,
                add_claim_note_200,
            ],
        )
        process_and_assert(machine, JobState.COMPLETED_SUCCESS)
        assert_requests_and_metrics_for_success(
            machine,
            metric_logger_distribution,
            metric_logger_increment,
            mock_hoppy_async_client,
            get_pending_contentions_increase_tinnitus_200,
            get_ep400_contentions_200,
        )

    @pytest.mark.parametrize(
        'pending_contentions_response',
        [
            pytest.param(get_contentions.Response(status_code=204, status_message='NO_CONTENT'), id='pending contentions NO_CONTENT'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK'), id='pending contentions OK, implicit none'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=None), id='pending contentions OK, explicit none'),
            pytest.param(get_contentions.Response(status_code=200, status_message='OK', contentions=[]), id='pending contentions OK, explicit empty list'),
        ],
    )
    class TestNoPendingClaimContentions:
        @pytest.mark.parametrize(
            'ep400_contentions_response',
            [
                pytest.param(
                    load_response(ep400_contentions_new_tinnitus_200, get_contentions.Response),
                    id='with single new contention from ep400',
                ),
                pytest.param(
                    load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response),
                    id='with single contention CFI from ep400',
                ),
                pytest.param(
                    load_response(ep400_contentions_increase_multicontention_200, get_contentions.Response),
                    id='with multiple contentions from ep400',
                ),
            ],
        )
        def test_process_succeeds_when_pending_claim_has_no_contentions(
            self,
            machine,
            mock_hoppy_async_client,
            metric_logger_distribution,
            metric_logger_increment,
            pending_contentions_response,
            ep400_contentions_response,
        ):
            mock_async_responses(
                mock_hoppy_async_client,
                [
                    get_pending_claim_200,
                    get_ep400_claim_200,
                    pending_contentions_response,
                    ep400_contentions_response,
                    get_pending_claim_200,
                    get_ep400_claim_200,
                    update_temporary_station_of_jurisdiction_200,
                    create_contentions_on_pending_claim_201,
                    cancel_claim_200,
                    add_claim_note_200,
                ],
            )
            process_and_assert(machine, JobState.COMPLETED_SUCCESS)
            assert_requests_and_metrics_for_success(
                machine, metric_logger_distribution, metric_logger_increment, mock_hoppy_async_client, pending_contentions_response, ep400_contentions_response
            )
