import asyncio
import pytest
from hoppy.async_hoppy_client import AsyncHoppyClient
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


def get_client(app_id="test", exchange_name="exchange", queue_name="queue",
               reply_queue="reply_queue", max_latency=3, requeue_attempts=3):
    exchange_props = ExchangeProperties(name=exchange_name, passive_declare=False)
    request_props = QueueProperties(name=queue_name, passive_declare=False)
    reply_props = QueueProperties(name=reply_queue, passive_declare=False)
    client = AsyncHoppyClient("test_client", app_id, {}, exchange_props, request_props, reply_props, queue_name,
                              reply_queue, max_latency, requeue_attempts)
    return client


@pytest.mark.asyncio
async def test_stop_with_caller_provided_event_loop(event_loop):
    # given
    client = get_client()

    # when
    await client.start(event_loop)
    await client.stop()

    # Then
    assert not event_loop.is_closed()


def test_stop_without_caller_provided_event_loop():
    # given
    client = get_client()
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)

    # when
    loop.run_until_complete(client.start(None))

    # Then
    with pytest.raises(RuntimeError, match='Event loop stopped before Future completed.'):
        loop.run_until_complete(client.stop())
