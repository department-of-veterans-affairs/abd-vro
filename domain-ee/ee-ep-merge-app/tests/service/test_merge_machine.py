import json
import os
import uuid
from unittest.mock import AsyncMock, Mock, call

import pytest
from hoppy.exception import ResponseException
from model import cancel_claim, get_contentions, update_contentions
from model import update_temp_station_of_jurisdiction as tsoj
from model.merge_job import JobState, MergeJob
from service.ep_merge_machine import CANCELLATION_REASON, EpMergeMachine
from util.contentions_util import ContentionsUtil, MergeException

JOB_ID = uuid.uuid4()
EP400_CLAIM_ID = 2
PENDING_CLAIM_ID = 1

RESPONSE_DIR = os.path.abspath('./tests/responses')
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_contentions_increase_tendinitis_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_contentions_increase_tinitus_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinitus_200.json'
pending_contentions_increase_tinitus_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinitus_200.json'
ep400_contentions_new_tinitus_200 = f'{RESPONSE_DIR}/claim_contentions_new_tinitus_200.json'
ep400_contentions_increase_multicontention_200 = f'{RESPONSE_DIR}/claim_contentions_increase_multicontention_200.json'


def load_response(file, response_type):
    try:
        with open(file) as f:
            return response_type.model_validate(json.load(f))
    except Exception as e:
        raise e


get_pending_contentions_req = get_contentions.Request(claim_id=PENDING_CLAIM_ID).model_dump(by_alias=True)
get_pending_contentions_200 = load_response(pending_contentions_increase_tendinitis_200, get_contentions.Response)
get_pending_contentions_increase_tinitus_200 = load_response(pending_contentions_increase_tinitus_200, get_contentions.Response)
get_ep400_contentions_req = get_contentions.Request(claim_id=EP400_CLAIM_ID).model_dump(by_alias=True)
get_ep400_contentions_200 = load_response(ep400_contentions_increase_tinitus_200, get_contentions.Response)
update_temporary_station_of_duty_req = tsoj.Request(claim_id=PENDING_CLAIM_ID,
                                                    temp_station_of_jurisdiction="398").model_dump(by_alias=True)
update_temporary_station_of_duty_200 = load_response(response_200, tsoj.Response)
update_pending_claim_req = update_contentions.Request(claim_id=PENDING_CLAIM_ID,
                                                      update_contentions=ContentionsUtil.merge_claims(
                                                          get_pending_contentions_200, get_ep400_contentions_200)
                                                      ).model_dump(by_alias=True)
update_pending_claim_200 = load_response(response_200, update_contentions.Response)
cancel_ep400_claim_req = cancel_claim.Request(claim_id=EP400_CLAIM_ID,
                                              lifecycle_status_reason_code="65",
                                              close_reason_text=CANCELLATION_REASON % PENDING_CLAIM_ID
                                              ).model_dump(by_alias=True)
cancel_claim_200 = load_response(response_200, cancel_claim.Response)


@pytest.fixture(autouse=True)
def mock_hoppy_async_client(mocker):
    return mocker.patch('hoppy.async_hoppy_client.RetryableAsyncHoppyClient')


@pytest.fixture(autouse=True)
def mock_hoppy_service_get_client(mocker, mock_hoppy_async_client):
    mocker.patch('src.python_src.service.ep_merge_machine.HOPPY.get_client').return_value = mock_hoppy_async_client


def test_constructor():
    merge_job = Mock()
    machine = EpMergeMachine(merge_job)

    assert machine.current_state_value == JobState.PENDING


@pytest.fixture
def machine():
    return EpMergeMachine(MergeJob(job_id=JOB_ID,
                                   pending_claim_id=PENDING_CLAIM_ID,
                                   ep400_claim_id=EP400_CLAIM_ID))


def get_mocked_async_response(side_effects):
    mock = AsyncMock()
    mock.side_effect = side_effects
    return mock


def mock_async_responses(mock_hoppy_async_client, responses):
    mock = AsyncMock()
    mock.side_effect = responses
    mock_hoppy_async_client.make_request = mock


def process_and_assert(machine, expected_state, expected_error_state):
    machine.process()
    assert machine.current_state_value == expected_state
    assert machine.job.state == expected_state
    assert machine.job.error_state == expected_error_state


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, get_contentions.Response), id="400"),
                             pytest.param(load_response(response_404, get_contentions.Response), id="404"),
                             pytest.param(load_response(response_500, get_contentions.Response), id="500")
                         ])
def test_invalid_request_at_get_pending_contentions(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client, invalid_request)
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS)
    mock_hoppy_async_client.make_request.assert_called_with(machine.job.job_id, get_pending_contentions_req)


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, get_contentions.Response), id="400"),
                             pytest.param(load_response(response_404, get_contentions.Response), id="404"),
                             pytest.param(load_response(response_500, get_contentions.Response), id="500")
                         ])
def test_invalid_request_at_get_ep400_contentions(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_EP400_CLAIM_CONTENTIONS)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req)
    ])


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, tsoj.Response), id="400"),
                             pytest.param(load_response(response_404, tsoj.Response), id="404"),
                             pytest.param(load_response(response_500, tsoj.Response), id="500")
                         ])
def test_invalid_request_at_set_temporary_station_of_duty(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req)
    ])


def test_process_fails_at_merge_contentions(machine, mock_hoppy_async_client, mocker):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200
                         ])
    mocker.patch('src.python_src.service.ep_merge_machine.ContentionsUtil.merge_claims',
                 side_effect=MergeException(message="Merge Oops"))

    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_MERGE_CONTENTIONS)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req)
    ])


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, update_contentions.Response), id="400"),
                             pytest.param(load_response(response_404, update_contentions.Response), id="404"),
                             pytest.param(load_response(response_500, update_contentions.Response), id="500")
                         ])
def test_invalid_request_at_update_contentions(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, update_pending_claim_req)
    ])


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, cancel_claim.Response), id="400"),
                             pytest.param(load_response(response_404, cancel_claim.Response), id="404"),
                             pytest.param(load_response(response_500, cancel_claim.Response), id="500")
                         ])
def test_invalid_request_at_cancel_claim_due_to_exception(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             update_pending_claim_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_CANCEL_EP400_CLAIM)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, update_pending_claim_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])


@pytest.mark.parametrize("get_contentions_res",
                         [
                             pytest.param((load_response(pending_contentions_increase_tendinitis_200, get_contentions.Response),
                                           load_response(ep400_contentions_increase_tinitus_200, get_contentions.Response)), id="different contention name"),

                             pytest.param((load_response(pending_contentions_increase_tinitus_200, get_contentions.Response),
                                          load_response(ep400_contentions_new_tinitus_200, get_contentions.Response)), id="different contention type"),

                             pytest.param((load_response(pending_contentions_increase_tinitus_200, get_contentions.Response),
                                          load_response(ep400_contentions_increase_multicontention_200, get_contentions.Response)), id="different contention name alongside duplicate")
                         ])
def test_process_succeeds_with_different_contention(machine, mock_hoppy_async_client, get_contentions_res):
    pending, ep400 = get_contentions_res
    update_pending_claim_req = update_contentions.Request(claim_id=PENDING_CLAIM_ID,
                                                          update_contentions=ContentionsUtil.merge_claims(
                                                              pending, ep400)
                                                          ).model_dump(by_alias=True)
    mock_async_responses(mock_hoppy_async_client,
                         [
                             pending,
                             ep400,
                             update_temporary_station_of_duty_200,
                             update_pending_claim_200,
                             cancel_claim_200
                         ])
    process_and_assert(machine, JobState.COMPLETED_SUCCESS, None)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, update_pending_claim_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])


def test_process_succeeds_with_duplicate_contention(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_contentions_increase_tinitus_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             cancel_claim_200
                         ])
    process_and_assert(machine, JobState.COMPLETED_SUCCESS, None)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])
