import asyncio
import json

from config import EXCHANGE
from hoppy import async_consumer, async_publisher
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties
from pika import BasicProperties

exchange_props = ExchangeProperties(name=EXCHANGE)


class MqEndpoint:
    auto_response_files = []

    def __init__(self, name, req_queue, response_queue):
        self.name = name
        self.index = 0
        queue_props = QueueProperties(name=req_queue, passive_declare=False)

        self.consumer = async_consumer.AsyncConsumer(exchange_properties=exchange_props,
                                                     queue_properties=queue_props,
                                                     routing_key=req_queue,
                                                     reply_callback=self._on_message)

        reply_props = QueueProperties(name=response_queue, passive_declare=False, auto_delete=True)
        self.producer = async_publisher.AsyncPublisher(exchange_properties=exchange_props,
                                                       queue_properties=reply_props,
                                                       routing_key=response_queue)

    async def start(self, event_loop):
        cons_connection = self.consumer.connect(event_loop)
        while not cons_connection.is_open:
            await asyncio.sleep(0)
        prod_connection = self.producer.connect(event_loop)
        while not prod_connection.is_open:
            await asyncio.sleep(0)

    def stop(self):
        self.consumer.stop()
        self.producer.stop()

    def _on_message(self, _channel, properties, delivery_tag, _body):
        correlation_id = properties.correlation_id

        self.consumer.acknowledge_message(delivery_tag)

        if len(self.auto_response_files) > 0:
            with open(self.auto_response_files[self.index]) as f:
                body = json.load(f)
                self.producer.publish_message(body,
                                              BasicProperties(app_id="Integration Test",
                                                              content_type="application/json",
                                                              correlation_id=correlation_id))
            if self.index + 1 != len(self.auto_response_files):
                self.index += 1
            else:
                self.index = 0

    def set_responses(self, auto_response_files=None):
        if auto_response_files is None:
            auto_response_files = []
        self.auto_response_files = auto_response_files
        self.index = 0
