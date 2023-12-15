import json
import os
import uuid
from unittest.mock import AsyncMock, Mock, call

import pytest
from hoppy.exception import ResponseException
from schema import (
    add_claim_note,
    cancel_claim,
    create_contentions,
    get_claim,
    get_contentions,
)
from schema import update_temp_station_of_jurisdiction as tsoj
from schema.claim import ClaimDetail
from schema.merge_job import JobState, MergeJob
from service.ep_merge_machine import (
    CANCEL_TRACKING_EP,
    CANCELLATION_REASON_FORMAT,
    EpMergeMachine,
)
from util.contentions_util import ContentionsUtil, MergeException

JOB_ID = uuid.uuid4()
PENDING_CLAIM_ID = 1
PENDING_CLAIM_EP_CODE = "010"
EP400_CLAIM_ID = 2
cancel_reason = CANCELLATION_REASON_FORMAT.format(ep_code=PENDING_CLAIM_EP_CODE, claim_id=PENDING_CLAIM_ID)

RESPONSE_DIR = os.path.abspath('./tests/responses')
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_201 = f'{RESPONSE_DIR}/201_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_claim_200 = f'{RESPONSE_DIR}/get_pending_claim_200.json'
pending_contentions_increase_tendinitis_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_contentions_increase_tinnitus_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_200.json'
pending_contentions_increase_tinnitus_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_200.json'
ep400_contentions_new_tinnitus_200 = f'{RESPONSE_DIR}/claim_contentions_new_tinnitus_200.json'
ep400_contentions_increase_multicontention_200 = f'{RESPONSE_DIR}/claim_contentions_increase_multicontention_200.json'


def load_response(file, response_type):
    try:
        with open(file) as f:
            return response_type.model_validate(json.load(f))
    except Exception as e:
        raise e


get_pending_claim_req = get_claim.Request(claim_id=PENDING_CLAIM_ID).model_dump(by_alias=True)
get_pending_claim_200 = load_response(pending_claim_200, get_claim.Response)
get_pending_contentions_req = get_contentions.Request(claim_id=PENDING_CLAIM_ID).model_dump(by_alias=True)
get_pending_contentions_200 = load_response(pending_contentions_increase_tendinitis_200, get_contentions.Response)
get_pending_contentions_increase_tinnitus_200 = load_response(pending_contentions_increase_tinnitus_200,
                                                              get_contentions.Response)
get_ep400_contentions_req = get_contentions.Request(claim_id=EP400_CLAIM_ID).model_dump(by_alias=True)
get_ep400_contentions_200 = load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response)
update_temporary_station_of_duty_req = tsoj.Request(claim_id=EP400_CLAIM_ID,
                                                    temp_station_of_jurisdiction="398").model_dump(by_alias=True)
update_temporary_station_of_duty_200 = load_response(response_200, tsoj.Response)
create_contentions_on_pending_claim_req = create_contentions.Request(claim_id=PENDING_CLAIM_ID,
                                                                     create_contentions=ContentionsUtil.merge_claims(
                                                                         get_pending_contentions_200,
                                                                         get_ep400_contentions_200)
                                                                     ).model_dump(by_alias=True)
create_contentions_on_pending_claim_201 = load_response(response_201, create_contentions.Response)
cancel_ep400_claim_req = cancel_claim.Request(claim_id=EP400_CLAIM_ID,
                                              lifecycle_status_reason_code=CANCEL_TRACKING_EP,
                                              close_reason_text=cancel_reason
                                              ).model_dump(by_alias=True)
cancel_claim_200 = load_response(response_200, cancel_claim.Response)
add_claim_note_req = add_claim_note.Request(vbms_claim_id=EP400_CLAIM_ID,
                                            claim_notes=[cancel_reason]).model_dump(by_alias=True)
add_claim_note_200 = load_response(response_200, add_claim_note.Response)


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
                             pytest.param(load_response(response_400, get_claim.Response), id="400"),
                             pytest.param(load_response(response_404, get_claim.Response), id="404"),
                             pytest.param(load_response(response_500, get_claim.Response), id="500"),
                             pytest.param(get_claim.Response(
                                 statusCode=200,
                                 statusMessage="OK",
                                 claim=ClaimDetail(claimId=3)
                             ).model_dump(), id="claim has no endProductCode")
                         ])
def test_invalid_request_at_get_pending_claim(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client, invalid_request)
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM)
    mock_hoppy_async_client.make_request.assert_called_with(machine.job.job_id, get_pending_claim_req)


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, get_contentions.Response), id="400"),
                             pytest.param(load_response(response_404, get_contentions.Response), id="404"),
                             pytest.param(load_response(response_500, get_contentions.Response), id="500")
                         ])
def test_invalid_request_at_get_pending_contentions(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_claim_200,
                             invalid_request
                         ])
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
                             get_pending_claim_200,
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
                             get_pending_claim_200,
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
                             get_pending_claim_200,
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
                             pytest.param(load_response(response_400, create_contentions.Response), id="400"),
                             pytest.param(load_response(response_404, create_contentions.Response), id="404"),
                             pytest.param(load_response(response_500, create_contentions.Response), id="500")
                         ])
def test_invalid_request_at_move_contentions_to_pending_claim(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_claim_200,
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_MOVE_CONTENTIONS_TO_PENDING_CLAIM)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, create_contentions_on_pending_claim_req)
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
                             get_pending_claim_200,
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             create_contentions_on_pending_claim_201,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_CANCEL_EP400_CLAIM)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, create_contentions_on_pending_claim_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])


@pytest.mark.parametrize("invalid_request",
                         [
                             pytest.param(ResponseException("Oops"), id="Caught Exception"),
                             pytest.param(load_response(response_400, add_claim_note.Response), id="400"),
                             pytest.param(load_response(response_500, add_claim_note.Response), id="500")
                         ])
def test_invalid_request_at_add_claim_note_due_to_exception(machine, mock_hoppy_async_client, invalid_request):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_claim_200,
                             get_pending_contentions_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             create_contentions_on_pending_claim_201,
                             cancel_claim_200,
                             invalid_request
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_ADD_CLAIM_NOTE_TO_EP400)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, create_contentions_on_pending_claim_req),
        call(machine.job.job_id, cancel_ep400_claim_req),
        call(machine.job.job_id, add_claim_note_req)
    ])


@pytest.mark.parametrize("get_contentions_res",
                         [
                             pytest.param(
                                 (load_response(pending_contentions_increase_tendinitis_200, get_contentions.Response),
                                  load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response)),
                                 id="different contention name"),

                             pytest.param(
                                 (load_response(pending_contentions_increase_tinnitus_200, get_contentions.Response),
                                  load_response(ep400_contentions_new_tinnitus_200, get_contentions.Response)),
                                 id="different contention type"),

                             pytest.param(
                                 (load_response(pending_contentions_increase_tinnitus_200, get_contentions.Response),
                                  load_response(ep400_contentions_increase_multicontention_200,
                                                get_contentions.Response)),
                                 id="different contention name alongside duplicate")
                         ])
def test_process_succeeds_with_different_contention(machine, mock_hoppy_async_client, get_contentions_res):
    pending_contentions, ep400_contentions = get_contentions_res
    create_pending_claim_req = create_contentions.Request(claim_id=PENDING_CLAIM_ID,
                                                          create_contentions=ContentionsUtil.merge_claims(
                                                              pending_contentions, ep400_contentions)
                                                          ).model_dump(by_alias=True)
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_claim_200,
                             pending_contentions,
                             ep400_contentions,
                             update_temporary_station_of_duty_200,
                             create_contentions_on_pending_claim_201,
                             cancel_claim_200,
                             add_claim_note_200
                         ])
    process_and_assert(machine, JobState.COMPLETED_SUCCESS, None)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, create_pending_claim_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])


def test_process_succeeds_with_duplicate_contention(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             get_pending_claim_200,
                             get_pending_contentions_increase_tinnitus_200,
                             get_ep400_contentions_200,
                             update_temporary_station_of_duty_200,
                             cancel_claim_200,
                             add_claim_note_200
                         ])
    process_and_assert(machine, JobState.COMPLETED_SUCCESS, None)
    mock_hoppy_async_client.make_request.assert_has_calls([
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, update_temporary_station_of_duty_req),
        call(machine.job.job_id, cancel_ep400_claim_req)
    ])
