import asyncio

import pytest
import pytest_asyncio
from integration.mq_endpoint import MqEndpoint
from src.python_src.api import on_shut_down, start_hoppy
from src.python_src.config import EXCHANGES, QUEUES, REPLY_QUEUES, ClientName


def pytest_collection_modifyitems(items):
    """Modifies test items in place to ensure test modules run in a given order:
    1. All test from modules not specified in the `module_order` below
    2. Test from each module in module_order (in order)
    """
    module_order = {"integration.test_get_endpoints": 1, "integration.test_merge_request": 2}
    item_order = {item: module_order.get(item.module.__name__, 0) for item in items}
    items.sort(key=item_order.get)


@pytest.fixture(autouse=True, scope="session")
def get_claim_endpoint():
    return create_mq_endpoint(ClientName.GET_CLAIM)


@pytest.fixture(autouse=True, scope="session")
def get_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.GET_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope="session")
def put_tsoj_endpoint():
    return create_mq_endpoint(ClientName.PUT_TSOJ)


@pytest.fixture(autouse=True, scope="session")
def create_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.CREATE_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope="session")
def update_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.UPDATE_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope="session")
def cancel_claim_endpoint():
    return create_mq_endpoint(ClientName.CANCEL_CLAIM)


@pytest.fixture(autouse=True, scope="session")
def add_claim_note_endpoint():
    return create_mq_endpoint(ClientName.BGS_ADD_CLAIM_NOTE)


def create_mq_endpoint(name):
    return MqEndpoint(name, EXCHANGES[name], QUEUES[name], REPLY_QUEUES[name])


@pytest_asyncio.fixture(autouse=True, scope="session")
async def endpoint_lifecycle(
    get_claim_endpoint: MqEndpoint,
    get_claim_contentions_endpoint: MqEndpoint,
    put_tsoj_endpoint: MqEndpoint,
    create_claim_contentions_endpoint: MqEndpoint,
    update_claim_contentions_endpoint: MqEndpoint,
    cancel_claim_endpoint: MqEndpoint,
    add_claim_note_endpoint: MqEndpoint,
):
    event_loop = asyncio.get_running_loop()
    await get_claim_endpoint.start(event_loop)
    await get_claim_contentions_endpoint.start(event_loop)
    await put_tsoj_endpoint.start(event_loop)
    await create_claim_contentions_endpoint.start(event_loop)
    await update_claim_contentions_endpoint.start(event_loop)
    await cancel_claim_endpoint.start(event_loop)
    await add_claim_note_endpoint.start(event_loop)
    await start_hoppy()

    yield

    get_claim_endpoint.stop()
    get_claim_contentions_endpoint.stop()
    put_tsoj_endpoint.stop()
    create_claim_contentions_endpoint.stop()
    update_claim_contentions_endpoint.stop()
    cancel_claim_endpoint.stop()
    add_claim_note_endpoint.stop()
    await on_shut_down()


@pytest.fixture(autouse=True)
def reset_responses(
    get_claim_endpoint: MqEndpoint,
    get_claim_contentions_endpoint: MqEndpoint,
    put_tsoj_endpoint: MqEndpoint,
    create_claim_contentions_endpoint: MqEndpoint,
    cancel_claim_endpoint: MqEndpoint,
):
    get_claim_endpoint.set_responses()
    get_claim_contentions_endpoint.set_responses()
    put_tsoj_endpoint.set_responses()
    create_claim_contentions_endpoint.set_responses()
    cancel_claim_endpoint.set_responses()
