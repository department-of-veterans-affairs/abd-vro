import json
import os
import uuid
from unittest.mock import ANY, AsyncMock, call

import pytest
from config import EP_MERGE_SPECIAL_ISSUE_CODE
from schema import (
    add_claim_note,
    cancel_claim,
    create_contentions,
    get_claim,
    get_contentions,
    update_contentions,
)
from schema import update_temp_station_of_jurisdiction as tsoj
from schema.merge_job import JobState
from service.ep_merge_machine import (
    CANCEL_TRACKING_EP,
    CANCELLATION_REASON_FORMAT,
    EP400_CFI_CONTENTIONS_METRIC,
    EP400_NEW_CONTENTIONS_METRIC,
    ERROR_STATES_TO_LOG_METRICS,
    JOB_DURATION_METRIC,
    JOB_ERROR_METRIC_PREFIX,
    JOB_FAILURE_METRIC,
    JOB_MERGED_CFI_CONTENTIONS_METRIC,
    JOB_MERGED_CONTENTIONS_METRIC,
    JOB_MERGED_NEW_CONTENTIONS_METRIC,
    JOB_SKIPPED_MERGE_METRIC,
    JOB_SUCCESS_METRIC,
    PENDING_CFI_CONTENTIONS_METRIC,
    PENDING_NEW_CONTENTIONS_METRIC,
)
from util.contentions_util import ContentionsUtil
from util.metric_logger import CountMetric, DistributionMetric

JOB_ID = uuid.uuid4()
PENDING_CLAIM_ID = 1
PENDING_CLAIM_EP_CODE = '010'
EP400_CLAIM_ID = 2
cancel_reason = CANCELLATION_REASON_FORMAT.format(ep_code=PENDING_CLAIM_EP_CODE, claim_id=PENDING_CLAIM_ID)

RESPONSE_DIR = os.path.abspath('./tests/responses')
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_201 = f'{RESPONSE_DIR}/201_response.json'
response_204 = f'{RESPONSE_DIR}/204_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_claim_200 = f'{RESPONSE_DIR}/get_pending_claim_200.json'
pending_contentions_increase_tendinitis_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_claim_200 = f'{RESPONSE_DIR}/get_ep400_claim_200.json'
ep400_contentions_increase_tinnitus_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_200.json'
ep400_contentions_increase_tinnitus_without_special_issues_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_without_special_issues_200.json'
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
get_pending_contentions_increase_tinnitus_200 = load_response(pending_contentions_increase_tinnitus_200, get_contentions.Response)
get_ep400_claim_req = get_claim.Request(claim_id=EP400_CLAIM_ID).model_dump(by_alias=True)
get_ep400_claim_200 = load_response(ep400_claim_200, get_claim.Response)
get_ep400_contentions_req = get_contentions.Request(claim_id=EP400_CLAIM_ID).model_dump(by_alias=True)
get_ep400_contentions_200 = load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response)
# Add special issue code to contention from config
get_ep400_contentions_200.contentions[0].special_issue_codes.append(EP_MERGE_SPECIAL_ISSUE_CODE)
get_ep400_contentions_204 = load_response(response_204, get_contentions.Response)
get_ep400_contentions_without_special_issues_200 = load_response(ep400_contentions_increase_tinnitus_200, get_contentions.Response)
update_temporary_station_of_jurisdiction_req = tsoj.Request(claim_id=EP400_CLAIM_ID, temp_station_of_jurisdiction='398').model_dump(by_alias=True)
revert_temporary_station_of_jurisdiction_req = tsoj.Request(claim_id=EP400_CLAIM_ID, temp_station_of_jurisdiction='111').model_dump(by_alias=True)
update_temporary_station_of_jurisdiction_200 = load_response(response_200, tsoj.Response)
revert_temporary_station_of_jurisdiction_200 = load_response(response_200, tsoj.Response)
create_contentions_on_pending_claim_req = create_contentions.Request(
    claim_id=PENDING_CLAIM_ID,
    create_contentions=ContentionsUtil.new_contentions(get_pending_contentions_200.contentions, get_ep400_contentions_200.contentions),
).model_dump(by_alias=True)
create_contentions_on_pending_claim_201 = load_response(response_201, create_contentions.Response)
update_contentions_on_ep400_req = update_contentions.Request(
    claim_id=EP400_CLAIM_ID, update_contentions=get_ep400_contentions_without_special_issues_200.contentions
).model_dump(by_alias=True)
update_contentions_on_ep400_200 = load_response(response_200, update_contentions.Response)

cancel_ep400_claim_req = cancel_claim.Request(
    claim_id=EP400_CLAIM_ID, lifecycle_status_reason_code=CANCEL_TRACKING_EP, close_reason_text=cancel_reason
).model_dump(by_alias=True)
cancel_claim_200 = load_response(response_200, cancel_claim.Response)
add_claim_note_req = add_claim_note.Request(vbms_claim_id=EP400_CLAIM_ID, claim_notes=[cancel_reason]).model_dump(by_alias=True)
add_claim_note_200 = load_response(response_200, add_claim_note.Response)


@pytest.fixture(autouse=True)
def mock_hoppy_async_client(mocker):
    return mocker.patch('hoppy.async_hoppy_client.RetryableAsyncHoppyClient')


@pytest.fixture(autouse=True)
def mock_hoppy_service_get_client(mocker, mock_hoppy_async_client):
    mocker.patch('src.python_src.service.ep_merge_machine.HOPPY.get_client').return_value = mock_hoppy_async_client


@pytest.fixture(autouse=True)
def mock_job_store(mocker):
    return mocker.patch('src.python_src.service.ep_merge_machine.JOB_STORE.update_merge_job')


@pytest.fixture(autouse=True)
def metric_logger_increment(mocker):
    return mocker.patch('service.ep_merge_machine.increment')


@pytest.fixture(autouse=True)
def metric_logger_distribution(mocker):
    return mocker.patch('service.ep_merge_machine.distribution')


def get_mocked_async_response(side_effects):
    mock = AsyncMock()
    mock.side_effect = side_effects
    return mock


def mock_async_responses(mock_hoppy_async_client, responses):
    mock = AsyncMock()
    mock.side_effect = responses
    mock_hoppy_async_client.make_request = mock


def process_and_assert(machine, expected_state: JobState, expected_error_state: JobState = None, num_errors: int = 0):
    machine.start()
    assert machine.current_state_value == expected_state
    assert machine.job.state == expected_state
    assert machine.job.error_state == expected_error_state
    if num_errors > 0:
        assert len(machine.job.messages) == num_errors


def assert_hoppy_requests(mock_hoppy_async_client, calls):
    # Number of expected calls should be equal number of actual calls
    assert len(calls) == len(mock_hoppy_async_client.make_request.call_args_list)
    # Assert calls are made in expected order
    mock_hoppy_async_client.make_request.assert_has_calls(calls)


def assert_metrics_called(
    metric_logger_distribution,
    metric_logger_increment,
    expected_completed_state: JobState,
    expected_error_state: JobState = None,
    expected_pending_new_contentions: int = 0,
    expected_pending_cfi_contentions: int = 0,
    expected_ep400_new_contentions: int = 0,
    expected_ep400_cfi_contentions: int = 0,
    expected_merge_contentions: int = 0,
    expected_merge_new_contentions: int = 0,
    expected_merge_cfi_contentions: int = 0,
    expected_merge_skip: bool = True,
):
    increment_metrics = []
    distribution_metrics = [DistributionMetric(JOB_DURATION_METRIC, ANY)]
    contention_metrics = [
        DistributionMetric(PENDING_NEW_CONTENTIONS_METRIC, expected_pending_new_contentions),
        DistributionMetric(PENDING_CFI_CONTENTIONS_METRIC, expected_pending_cfi_contentions),
        DistributionMetric(EP400_NEW_CONTENTIONS_METRIC, expected_ep400_new_contentions),
        DistributionMetric(EP400_CFI_CONTENTIONS_METRIC, expected_ep400_cfi_contentions),
        DistributionMetric(JOB_MERGED_CONTENTIONS_METRIC, expected_merge_contentions),
        DistributionMetric(JOB_MERGED_NEW_CONTENTIONS_METRIC, expected_merge_new_contentions),
        DistributionMetric(JOB_MERGED_CFI_CONTENTIONS_METRIC, expected_merge_cfi_contentions),
    ]
    if expected_completed_state == JobState.COMPLETED_SUCCESS:
        increment_metrics.append(CountMetric(JOB_SUCCESS_METRIC))
        distribution_metrics.extend(contention_metrics)
        if expected_merge_skip:
            increment_metrics.append(CountMetric(JOB_SKIPPED_MERGE_METRIC))

    else:
        increment_metrics.extend([CountMetric(JOB_FAILURE_METRIC), CountMetric(f'{JOB_ERROR_METRIC_PREFIX}.{expected_error_state}')])
        if expected_error_state in ERROR_STATES_TO_LOG_METRICS:
            distribution_metrics.extend(contention_metrics)
            if expected_merge_skip:
                increment_metrics.append(CountMetric(JOB_SKIPPED_MERGE_METRIC))

    metric_logger_increment.assert_has_calls([call(increment_metrics)])
    metric_logger_distribution.assert_has_calls([call(distribution_metrics)])


def assert_requests_and_metrics_for_success(
    machine, metric_logger_distribution, metric_logger_increment, mock_hoppy_async_client, pending_contentions_response, ep400_contentions_response
):
    num_pending_new_contentions = sum(1 for c in pending_contentions_response.contentions or [] if c.contention_type_code == 'NEW')
    num_pending_cfi_contentions = sum(1 for c in pending_contentions_response.contentions or [] if c.contention_type_code == 'INCREASE')
    num_ep400_new_contentions = sum(1 for c in ep400_contentions_response.contentions if c.contention_type_code == 'NEW')
    num_ep400_cfi_contentions = sum(1 for c in ep400_contentions_response.contentions if c.contention_type_code == 'INCREASE')

    merged_contentions = ContentionsUtil.new_contentions(pending_contentions_response.contentions, ep400_contentions_response.contentions)
    num_merged_new_contentions = sum(1 for c in merged_contentions if c.contention_type_code == 'NEW')
    num_merged_cfi_contentions = sum(1 for c in merged_contentions if c.contention_type_code == 'INCREASE')
    merge_skipped = not merged_contentions

    requests = [
        call(machine.job.job_id, get_pending_claim_req),
        call(machine.job.job_id, get_ep400_claim_req),
        call(machine.job.job_id, get_pending_contentions_req),
        call(machine.job.job_id, get_ep400_contentions_req),
        call(machine.job.job_id, get_pending_claim_req),
        call(machine.job.job_id, update_temporary_station_of_jurisdiction_req),
    ]
    if not merge_skipped:
        create_pending_claim_req = create_contentions.Request(claim_id=PENDING_CLAIM_ID, create_contentions=merged_contentions).model_dump(by_alias=True)
        requests.append(call(machine.job.job_id, create_pending_claim_req))
    requests.extend(
        [
            call(machine.job.job_id, cancel_ep400_claim_req),
            call(machine.job.job_id, add_claim_note_req),
        ]
    )

    assert_hoppy_requests(
        mock_hoppy_async_client,
        requests,
    )
    assert_metrics_called(
        metric_logger_distribution,
        metric_logger_increment,
        JobState.COMPLETED_SUCCESS,
        None,
        expected_pending_new_contentions=num_pending_new_contentions,
        expected_pending_cfi_contentions=num_pending_cfi_contentions,
        expected_ep400_new_contentions=num_ep400_new_contentions,
        expected_ep400_cfi_contentions=num_ep400_cfi_contentions,
        expected_merge_contentions=len(merged_contentions),
        expected_merge_new_contentions=num_merged_new_contentions,
        expected_merge_cfi_contentions=num_merged_cfi_contentions,
        expected_merge_skip=not merged_contentions,
    )
