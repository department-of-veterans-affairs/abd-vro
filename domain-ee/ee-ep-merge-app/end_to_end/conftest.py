import asyncio
from uuid import uuid4

import pytest
import pytest_asyncio
from hoppy.async_hoppy_client import AsyncHoppyClient
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties

from src.python_src.api import on_shut_down, start_hoppy, start_job_runner
from src.python_src.config import BIP_EXCHANGE
from src.python_src.schema.merge_job import JobState
from src.python_src.schema.response import GeneralResponse
from src.python_src.service.job_store import JOB_STORE


@pytest.fixture(scope='session')
def lifecycle_endpoint():
    client = AsyncHoppyClient(
        name='putClaimLifecycleStatusQueueClient',
        app_id='EP_MERGE',
        config={},
        exchange_properties=ExchangeProperties(name=BIP_EXCHANGE, passive_declare=True),
        request_queue_properties=QueueProperties(name='svc_bip_api.put_claim_lifecycle_status', passive_declare=True),
        request_routing_key='svc_bip_api.put_claim_lifecycle_status',
        reply_queue_properties=QueueProperties(name='ep_merge.put_claim_lifecycle_status_response', passive_declare=False),
        reply_routing_key='ep_merge.put_claim_lifecycle_status_response',
        request_message_ttl=0,
        response_max_latency=3,
        response_reject_and_requeue_attempts=3,
    )
    return client


@pytest_asyncio.fixture(autouse=True, scope='session')
async def endpoint_lifecycle(lifecycle_endpoint):
    await lifecycle_endpoint.start(asyncio.get_event_loop())
    while not lifecycle_endpoint.is_ready():
        await asyncio.sleep(0)

    await start_hoppy()
    await start_job_runner()
    yield
    await lifecycle_endpoint.stop()
    await on_shut_down()


def assert_response(
    response,
    pending_claim_id,
    ep400_claim_id,
    expected_state: JobState,
    expected_error_state: JobState | None = None,
    expected_num_errors: int = 0,
    status_code: int = 200,
):
    assert response.status_code == status_code
    response_json = response.json()
    assert response_json is not None
    job = response_json['job']
    assert job['pending_claim_id'] == pending_claim_id
    assert job['ep400_claim_id'] == ep400_claim_id
    assert job['state'] == expected_state
    if expected_error_state is None:
        assert 'error_state' not in job
    else:
        assert job['error_state'] == expected_error_state
    if expected_num_errors == 0:
        assert 'messages' not in job
    else:
        assert len(job['messages']) == expected_num_errors
    return response_json


def assert_job(job_id, pending_claim_id, ep400_claim_id, expected_state: JobState, expected_error_state: JobState | None = None, expected_num_errors: int = 0):
    job = JOB_STORE.get_merge_job(job_id)
    assert job is not None
    assert job.pending_claim_id == pending_claim_id
    assert job.ep400_claim_id == ep400_claim_id
    assert job.state == expected_state
    assert job.error_state == expected_error_state

    if expected_num_errors == 0:
        assert job.messages is None
    else:
        assert len(job.messages) == expected_num_errors


async def reset_claim(ep400_claim_id, lifecycle_endpoint):
    response = await lifecycle_endpoint.make_request(uuid4(), {'claimId': ep400_claim_id, 'claimLifecycleStatus': 'Open'})
    response = GeneralResponse(**response)
    assert response.status_code == 200
