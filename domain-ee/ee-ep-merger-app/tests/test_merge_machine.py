import json
import uuid
from unittest.mock import AsyncMock, Mock

import pytest
from hoppy.exception import ResponseException
from src.python_src.model import (cancel_claim, get_contentions,
                                  update_contentions)
from src.python_src.model import update_temp_station_of_jurisdiction as tsoj
from src.python_src.service.claims_util import MergeException
from src.python_src.service.ep_merge_machine import EpMergeMachine
from src.python_src.service.merge_job import JobState, MergeJob


@pytest.fixture(autouse=True)
def mock_hoppy_async_client(mocker):
    return mocker.patch('hoppy.async_hoppy_client.RetryableAsyncHoppyClient')


@pytest.fixture(autouse=True)
def mock_hoppy_service(mocker):
    return mocker.patch('src.python_src.service.hoppy_service.HoppyService')


@pytest.fixture(autouse=True)
def mock_hoppy_service_get_client(mock_hoppy_service, mock_hoppy_async_client):
    mock_hoppy_service.get_client.return_value = mock_hoppy_async_client


def test_constructor(mock_hoppy_service):
    merge_job = Mock()
    machine = EpMergeMachine(mock_hoppy_service, merge_job)

    assert machine.current_state_value == JobState.PENDING


def load_response(file, response_type):
    with open(file) as f:
        return response_type.model_validate(json.load(f))


job_id = uuid.uuid4()
pending_claim_id = 1
supp_claim_id = 2

update_temporary_station_of_duty_200 = load_response('put_response_200.json', tsoj.Response)
update_temporary_station_of_duty_404 = load_response('404_response.json', tsoj.Response)
get_pending_contentions_200 = load_response('get_pending_claim_contentions_200.json', get_contentions.Response)
get_supp_contentions_200 = load_response('get_supp_claim_contentions_200.json', get_contentions.Response)
update_pending_claim_200 = load_response('put_response_200.json', update_contentions.Response)
cancel_claim_200 = load_response('put_response_200.json', cancel_claim.Response)


@pytest.fixture
def machine(mock_hoppy_service):
    return EpMergeMachine(mock_hoppy_service,
                          MergeJob(job_id=job_id,
                                   pending_claim_id=pending_claim_id,
                                   supp_claim_id=supp_claim_id)
                          )


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


def test_pending_claim_does_not_exist(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client, update_temporary_station_of_duty_404)
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION)


def test_process_fails_at_set_temporary_station_of_duty(machine, mock_hoppy_service, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client, ResponseException("Temporary Station of Jurisdiction Oops"))
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION)


def test_process_fails_at_get_pending_contentions(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             ResponseException("Pending Claims Oops")
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS)


def test_process_fails_at_get_supplemental_contentions(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             get_pending_contentions_200,
                             ResponseException("Supplemental Claims Oops")
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_GET_SUPP_CLAIM_CONTENTIONS)


def test_process_fails_at_merge_contentions(machine, mock_hoppy_async_client, mocker):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             get_pending_contentions_200,
                             get_supp_contentions_200,
                         ])
    mocker.patch('src.python_src.service.ep_merge_machine.ClaimsUtil.merge_claims',
                 side_effect=MergeException(message="Merge Oops"))

    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_MERGE_CONTENTIONS)


def test_process_fails_at_update_contentions(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             get_pending_contentions_200,
                             get_supp_contentions_200,
                             ResponseException("oops")
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS)


def test_process_fails_at_cancel_claim_due_to_exception(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             get_pending_contentions_200,
                             get_supp_contentions_200,
                             update_pending_claim_200,
                             ResponseException("oops")
                         ])
    process_and_assert(machine, JobState.COMPLETED_ERROR, JobState.RUNNING_CANCEL_SUPP_CLAIM)


def test_process_succeeds(machine, mock_hoppy_async_client):
    mock_async_responses(mock_hoppy_async_client,
                         [
                             update_temporary_station_of_duty_200,
                             get_pending_contentions_200,
                             get_supp_contentions_200,
                             update_pending_claim_200,
                             cancel_claim_200
                         ])
    process_and_assert(machine, JobState.COMPLETED_SUCCESS, None)
