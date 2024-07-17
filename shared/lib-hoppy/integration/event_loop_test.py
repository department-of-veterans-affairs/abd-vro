import asyncio

import pytest
from hoppy.async_hoppy_client import AsyncHoppyClient
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


def get_client() -> AsyncHoppyClient:
    request_queue = "queue"
    reply_queue = "reply_queue"
    exchange_props = ExchangeProperties(name="exchange", passive_declare=False)
    request_props = QueueProperties(name=request_queue, passive_declare=False)
    reply_props = QueueProperties(name=reply_queue, passive_declare=False)
    client = AsyncHoppyClient(
        name="test_client",
        app_id="test",
        config={},
        exchange_properties=exchange_props,
        request_queue_properties=request_props,
        reply_queue_properties=reply_props,
        request_routing_key=request_queue,
        reply_routing_key=reply_queue,
        request_message_ttl=0,
        response_max_latency=3,
        response_reject_and_requeue_attempts=3,
    )
    return client


@pytest.mark.asyncio
async def test_stop_with_caller_provided_event_loop():
    # given
    client = get_client()

    # when
    event_loop = asyncio.get_running_loop()
    await client.start(event_loop)
    assert client.is_ready()
    await client.stop()

    # Then
    assert not event_loop.is_closed()
    assert not client.is_ready()


@pytest.mark.asyncio
async def test_stop_with_different_event_loop():
    # given
    client = get_client()

    # when
    event_loop = asyncio.get_running_loop()
    await client.start(event_loop)
    assert client.is_ready()

    new_event_loop = asyncio.new_event_loop()
    # Then
    with pytest.raises(RuntimeError, match='Cannot run the event loop while another loop is running'):
        await new_event_loop.run_until_complete(await client.stop())
