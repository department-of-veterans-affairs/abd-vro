import asyncio

import pytest
import pytest_asyncio

from integration.mq_endpoint import MqEndpoint
from src.python_src.api import on_shut_down, start_hoppy, start_job_runner
from src.python_src.config import CLIENTS, ClientName

RESPONSE_DIR = './tests/responses'
response_200 = f'{RESPONSE_DIR}/200_response.json'
response_201 = f'{RESPONSE_DIR}/201_response.json'
response_204 = f'{RESPONSE_DIR}/204_response.json'
response_404 = f'{RESPONSE_DIR}/404_response.json'
response_400 = f'{RESPONSE_DIR}/400_response.json'
response_500 = f'{RESPONSE_DIR}/500_response.json'
pending_claim_200 = f'{RESPONSE_DIR}/get_pending_claim_200.json'
pending_claim_200_closed = f'{RESPONSE_DIR}/get_pending_claim_200_closed.json'
pending_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_claim_200 = f'{RESPONSE_DIR}/get_ep400_claim_200.json'
ep400_claim_200_closed = f'{RESPONSE_DIR}/get_ep400_claim_200_closed.json'
ep400_claim_200_missing_ep_code = f'{RESPONSE_DIR}/get_ep400_claim_200_missing_ep_code.json'
ep400_claim_200_unsupported_ep_code = f'{RESPONSE_DIR}/get_ep400_claim_200_unsupported_ep_code.json'
ep400_claim_200_missing_claim_type_code = f'{RESPONSE_DIR}/get_ep400_claim_200_missing_claim_type_code.json'
ep400_claim_200_unsupported_claim_type_code = f'{RESPONSE_DIR}/get_ep400_claim_200_unsupported_claim_type_code.json'
ep400_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tinnitus_200.json'
ep400_duplicate_contentions_200 = f'{RESPONSE_DIR}/claim_contentions_increase_tendinitis_200.json'
ep400_claim_contentions_deactive_special_issue_code_200 = f'{RESPONSE_DIR}/claim_contentions_deactive_special_issue_code_200.json'
get_special_issue_types_200 = f'{RESPONSE_DIR}/get_special_issue_types_200.json'


def pytest_collection_modifyitems(items):
    """Modifies test items in place to ensure test modules run in a given order:
    1. All test from modules not specified in the `module_order` below
    2. Test from each module in module_order (in order)
    """
    module_order = {'integration.test_get_endpoints': 1, 'integration.test_merge_request': 2}
    item_order = {item: module_order.get(item.module.__name__, 0) for item in items}
    items.sort(key=item_order.get)


@pytest.fixture(autouse=True, scope='session')
def get_claim_endpoint():
    return create_mq_endpoint(ClientName.GET_CLAIM)


@pytest.fixture(autouse=True, scope='session')
def get_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.GET_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope='session')
def get_special_issue_types_endpoint():
    return create_mq_endpoint(ClientName.GET_SPECIAL_ISSUE_TYPES)


@pytest.fixture(autouse=True, scope='session')
def put_tsoj_endpoint():
    return create_mq_endpoint(ClientName.PUT_TSOJ)


@pytest.fixture(autouse=True, scope='session')
def create_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.CREATE_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope='session')
def update_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.UPDATE_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope='session')
def cancel_claim_endpoint():
    return create_mq_endpoint(ClientName.CANCEL_CLAIM)


@pytest.fixture(autouse=True, scope='session')
def add_claim_note_endpoint():
    return create_mq_endpoint(ClientName.BGS_ADD_CLAIM_NOTE)


def create_mq_endpoint(name: ClientName) -> MqEndpoint:
    return MqEndpoint(name, CLIENTS[name].exchange, CLIENTS[name].request_queue, CLIENTS[name].response_queue)


@pytest_asyncio.fixture(autouse=True, scope='session')
async def endpoint_lifecycle(
    get_claim_endpoint: MqEndpoint,
    get_claim_contentions_endpoint: MqEndpoint,
    get_special_issue_types_endpoint: MqEndpoint,
    put_tsoj_endpoint: MqEndpoint,
    create_claim_contentions_endpoint: MqEndpoint,
    update_claim_contentions_endpoint: MqEndpoint,
    cancel_claim_endpoint: MqEndpoint,
    add_claim_note_endpoint: MqEndpoint,
):
    event_loop = asyncio.get_running_loop()
    await get_claim_endpoint.start(event_loop)
    await get_claim_contentions_endpoint.start(event_loop)
    await get_special_issue_types_endpoint.start(event_loop)
    await put_tsoj_endpoint.start(event_loop)
    await create_claim_contentions_endpoint.start(event_loop)
    await update_claim_contentions_endpoint.start(event_loop)
    await cancel_claim_endpoint.start(event_loop)
    await add_claim_note_endpoint.start(event_loop)
    await start_hoppy()
    await start_job_runner()

    yield

    get_claim_endpoint.stop()
    get_claim_contentions_endpoint.stop()
    get_special_issue_types_endpoint.stop()
    put_tsoj_endpoint.stop()
    create_claim_contentions_endpoint.stop()
    update_claim_contentions_endpoint.stop()
    cancel_claim_endpoint.stop()
    add_claim_note_endpoint.stop()
    await on_shut_down()


@pytest.fixture(autouse=True)
def reset_responses_to_success(
    get_claim_endpoint: MqEndpoint,
    get_claim_contentions_endpoint: MqEndpoint,
    get_special_issue_types_endpoint: MqEndpoint,
    put_tsoj_endpoint: MqEndpoint,
    create_claim_contentions_endpoint: MqEndpoint,
    update_claim_contentions_endpoint: MqEndpoint,
    cancel_claim_endpoint: MqEndpoint,
    add_claim_note_endpoint: MqEndpoint,
):
    get_claim_endpoint.set_responses([pending_claim_200, ep400_claim_200, pending_claim_200, ep400_claim_200])
    get_claim_contentions_endpoint.set_responses([pending_contentions_200, ep400_contentions_200])
    get_special_issue_types_endpoint.set_responses([get_special_issue_types_200])
    put_tsoj_endpoint.set_responses([response_200])
    create_claim_contentions_endpoint.set_responses([response_201])
    update_claim_contentions_endpoint.set_responses()  # No response expected for successful merge request
    cancel_claim_endpoint.set_responses([response_200])
    add_claim_note_endpoint.set_responses([response_200])
