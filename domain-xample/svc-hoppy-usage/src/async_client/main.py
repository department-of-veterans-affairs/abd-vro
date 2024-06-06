import asyncio
from config import exchange_properties, request_queue_properties, reply_queue_properties, REQUEST_QUEUE, REPLY_QUEUE
from hoppy.async_hoppy_client import AsyncPublisher, RetryableAsyncHoppyClient
from hoppy.config import RABBITMQ_CONFIG
from downstream_service import DownStreamService

async_requester = AsyncPublisher(RABBITMQ_CONFIG, exchange_properties, request_queue_properties, REQUEST_QUEUE)
async_client = RetryableAsyncHoppyClient(
    app_id="xample",
    name="xample-async-client",
    config=RABBITMQ_CONFIG,
    exchange_properties=exchange_properties,
    request_queue_properties=request_queue_properties,
    reply_queue_properties=reply_queue_properties,
    request_routing_key=REQUEST_QUEUE,
    reply_routing_key=REPLY_QUEUE,
    max_latency=3,
    response_reject_and_requeue_attempts=3,
    max_retries=3,
)

downstream_service = DownStreamService()


async def start_connections(event_loop):
    await async_client.start(event_loop)
    await downstream_service.start(event_loop)
    async_requester.connect(event_loop)


async def stop_connections():
    await async_client.stop()
    downstream_service.stop()
    async_requester.stop()


async def main():
    await start_connections(asyncio.get_event_loop())
    response = await async_client.make_request("test", "test")
    try:
        assert response == "expected"
    except AssertionError:
        print(f"Expected response: 'expected', got: {response}")
    await stop_connections()


if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(main())
