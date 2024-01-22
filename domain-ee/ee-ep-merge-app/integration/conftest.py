import asyncio

import pytest
import pytest_asyncio
from integration.mq_endpoint import MqEndpoint
from src.python_src.api import on_shut_down, on_start_up
from src.python_src.config import EXCHANGES, QUEUES, REPLY_QUEUES, ClientName


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
async def endpoint_lifecycle(get_claim_endpoint: MqEndpoint,
                             get_claim_contentions_endpoint: MqEndpoint,
                             put_tsoj_endpoint: MqEndpoint,
                             create_claim_contentions_endpoint: MqEndpoint,
                             update_claim_contentions_endpoint: MqEndpoint,
                             cancel_claim_endpoint: MqEndpoint,
                             add_claim_note_endpoint: MqEndpoint):
    event_loop = asyncio.get_running_loop()
    await get_claim_endpoint.start(event_loop)
    await get_claim_contentions_endpoint.start(event_loop)
    await put_tsoj_endpoint.start(event_loop)
    await create_claim_contentions_endpoint.start(event_loop)
    await update_claim_contentions_endpoint.start(event_loop)
    await cancel_claim_endpoint.start(event_loop)
    await add_claim_note_endpoint.start(event_loop)
    await on_start_up()
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
def reset_responses(get_claim_endpoint: MqEndpoint,
                    get_claim_contentions_endpoint: MqEndpoint,
                    put_tsoj_endpoint: MqEndpoint,
                    create_claim_contentions_endpoint: MqEndpoint,
                    cancel_claim_endpoint: MqEndpoint):
    get_claim_endpoint.set_responses()
    get_claim_contentions_endpoint.set_responses()
    put_tsoj_endpoint.set_responses()
    create_claim_contentions_endpoint.set_responses()
    cancel_claim_endpoint.set_responses()
