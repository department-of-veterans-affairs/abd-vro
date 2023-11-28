import pytest
import pytest_asyncio
from integration.mq_endpoint import MqEndpoint
from src.python_src.config import QUEUES, REPLY_QUEUES, ClientName


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
def update_claim_contentions_endpoint():
    return create_mq_endpoint(ClientName.UPDATE_CLAIM_CONTENTIONS)


@pytest.fixture(autouse=True, scope="session")
def cancel_claim_endpoint():
    return create_mq_endpoint(ClientName.CANCEL_CLAIM)


def create_mq_endpoint(name):
    return MqEndpoint(name, QUEUES[name], REPLY_QUEUES[name])


@pytest_asyncio.fixture(autouse=True, scope="session")
async def endpoint_lifecycle(event_loop,
                             get_claim_endpoint: MqEndpoint,
                             get_claim_contentions_endpoint: MqEndpoint,
                             put_tsoj_endpoint: MqEndpoint,
                             update_claim_contentions_endpoint: MqEndpoint,
                             cancel_claim_endpoint: MqEndpoint):
    await get_claim_endpoint.start(event_loop)
    await get_claim_contentions_endpoint.start(event_loop)
    await put_tsoj_endpoint.start(event_loop)
    await update_claim_contentions_endpoint.start(event_loop)
    await cancel_claim_endpoint.start(event_loop)

    yield

    get_claim_endpoint.stop()
    get_claim_contentions_endpoint.stop()
    put_tsoj_endpoint.stop()
    update_claim_contentions_endpoint.stop()
    cancel_claim_endpoint.stop()
