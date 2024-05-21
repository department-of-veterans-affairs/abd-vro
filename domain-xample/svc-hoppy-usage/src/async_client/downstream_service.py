import asyncio
from config import exchange_properties, request_queue_properties, reply_queue_properties, REQUEST_QUEUE, REPLY_QUEUE
from hoppy.async_hoppy_client import AsyncPublisher, AsyncConsumer
from hoppy.config import RABBITMQ_CONFIG


class DownStreamService:

    def __init__(self):
        self.async_reply_publisher = AsyncPublisher(RABBITMQ_CONFIG, exchange_properties, reply_queue_properties, REPLY_QUEUE)
        self.async_request_consumer = AsyncConsumer(
            RABBITMQ_CONFIG, exchange_properties, request_queue_properties, REQUEST_QUEUE, reply_callback=self._on_message
        )

    def _on_message(self, _channel, properties, delivery_tag, _body):
        _channel.basic_ack(delivery_tag)
        self.async_reply_publisher.publish_message("expected", properties)

    async def start(self, loop):
        self.async_reply_publisher.connect(loop)
        self.async_request_consumer.connect(loop)
        while not self.async_reply_publisher.is_ready and not self.async_request_consumer.is_ready:
            await asyncio.sleep(1)

    def stop(self):
        self.async_reply_publisher.stop()
        self.async_request_consumer.stop()
