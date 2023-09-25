import uuid
from unittest.mock import AsyncMock, Mock

import pytest
import src
from hoppy.exception import ResponseException
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


job_id = uuid.uuid4()
pending_claim_id = 1
supp_claim_id = 2
valid_set_temporary_station_of_duty_response = '{}'
valid_get_pending_contentions_response = '{}'
valid_get_supplemental_contentions_response = '{}'
valid_update_pending_claim_response = '{}'
valid_update_cancel_claim_response = '{}'


def create_machine(mock_hoppy_service):
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


def test_process_fails_at_set_temporary_station_of_duty(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client, ResponseException("Oops"))

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_SET_TEMP_STATION_OF_JURISDICTION


def test_process_fails_at_get_pending_contentions(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             ResponseException("Oops")
                         ])

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_GET_PENDING_CLAIM_CONTENTIONS


def test_process_fails_at_get_supplemental_contentions(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             valid_get_pending_contentions_response,
                             ResponseException("oops")
                         ])

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_GET_SUPP_CLAIM_CONTENTIONS


def test_process_fails_at_merge_contentions(mocker, mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             valid_get_pending_contentions_response,
                             valid_get_supplemental_contentions_response,
                         ])
    mocker.patch.object(src.python_src.service.ep_merge_machine.ClaimsUtil, 'merge_claims',
                        side_effect=MergeException(message="Merge Oops"))

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_MERGE_CONTENTIONS


def test_process_fails_at_update_contentions(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             valid_get_pending_contentions_response,
                             valid_get_supplemental_contentions_response,
                             ResponseException("oops")
                         ])

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_UPDATE_PENDING_CLAIM_CONTENTIONS


def test_process_fails_at_cancel_claim(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             valid_get_pending_contentions_response,
                             valid_get_supplemental_contentions_response,
                             valid_update_pending_claim_response,
                             ResponseException("oops")
                         ])

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_ERROR
    assert machine.job.state == JobState.COMPLETED_ERROR
    assert machine.job.error_state == JobState.RUNNING_CANCEL_SUPP_CLAIM


def test_process_succeeds(mock_hoppy_service, mock_hoppy_async_client):
    machine = create_machine(mock_hoppy_service)

    mock_async_responses(mock_hoppy_async_client,
                         [
                             valid_set_temporary_station_of_duty_response,
                             valid_get_pending_contentions_response,
                             valid_get_supplemental_contentions_response,
                             valid_update_pending_claim_response,
                             valid_update_cancel_claim_response
                         ])

    machine.process()
    assert machine.current_state_value == JobState.COMPLETED_SUCCESS
    assert machine.job.state == JobState.COMPLETED_SUCCESS
    assert machine.job.error_state is None
