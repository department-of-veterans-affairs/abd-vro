import pytest_asyncio
from config import QUEUES, REPLY_QUEUES, ClientName
from integration.mq_endpoint import MqEndpoint


@pytest_asyncio.fixture(autouse=True, scope="session")
def get_claim_contentions_endpoint():
    name = ClientName.GET_CLAIM_CONTENTIONS
    endpoint = MqEndpoint(name, QUEUES[name], REPLY_QUEUES[name])
    return endpoint


@pytest_asyncio.fixture(autouse=True, scope="session")
async def put_tsoj_endpoint():
    name = ClientName.PUT_TSOJ
    endpoint = MqEndpoint(name, QUEUES[name], REPLY_QUEUES[name])
    return endpoint


@pytest_asyncio.fixture(autouse=True, scope="session")
async def update_claim_contentions_endpoint():
    name = ClientName.UPDATE_CLAIM_CONTENTIONS
    endpoint = MqEndpoint(name, QUEUES[name], REPLY_QUEUES[name])
    return endpoint


@pytest_asyncio.fixture(autouse=True, scope="session")
async def cancel_claim_endpoint():
    name = ClientName.CANCEL_CLAIM
    endpoint = MqEndpoint(name, QUEUES[name], REPLY_QUEUES[name])
    return endpoint


@pytest_asyncio.fixture(autouse=True, scope="session")
async def endpoint_lifecycle(event_loop,
                             get_claim_contentions_endpoint: MqEndpoint,
                             put_tsoj_endpoint: MqEndpoint,
                             update_claim_contentions_endpoint: MqEndpoint,
                             cancel_claim_endpoint: MqEndpoint):
    await get_claim_contentions_endpoint.start(event_loop)
    await put_tsoj_endpoint.start(event_loop)
    await update_claim_contentions_endpoint.start(event_loop)
    await cancel_claim_endpoint.start(event_loop)

    yield

    get_claim_contentions_endpoint.stop()
    put_tsoj_endpoint.stop()
    update_claim_contentions_endpoint.stop()
    cancel_claim_endpoint.stop()
