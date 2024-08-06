import asyncio
import json

from hoppy import async_consumer, async_publisher
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties
from pika import BasicProperties
from src.python_src.config import ClientName


class MqEndpointConsumerException(Exception):
    pass


class MqEndpoint:
    def __init__(self, name, exchange, req_queue, response_queue, arguments = {}):
        self.name = name
        self.index = 0
        self.auto_response_files = []
        type = 'direct'
        if (name == ClientName.DEAD_LETTER):
            type = 'fanout'

        exchange_props = ExchangeProperties(name=exchange, auto_delete=True, passive_declare=False, type=type)
        queue_props = QueueProperties(name=req_queue, auto_delete=True, passive_declare=False, arguments=arguments)
        self.consumer = async_consumer.AsyncConsumer(
            exchange_properties=exchange_props, queue_properties=queue_props, routing_key=req_queue, reply_callback=self._on_message
        )
        if name != ClientName.DEAD_LETTER:
            reply_props = QueueProperties(name=response_queue, passive_declare=False, auto_delete=True)
            self.publisher = async_publisher.AsyncPublisher(exchange_properties=exchange_props, queue_properties=reply_props, routing_key=response_queue)

    async def start(self, event_loop):
        cons_connection = self.consumer.connect(event_loop)
        pub_connection = self.publisher.connect(event_loop)

        attempt = 0
        while attempt < 3:
            if cons_connection.is_open and pub_connection.is_open:
                break
            else:
                attempt += 1
            await asyncio.sleep(1)
        if not cons_connection.is_open or not pub_connection.is_open:
            raise Exception(f'Could not connect to MqEndpoint={self.name}')

    def stop(self):
        self.consumer.stop()
        self.publisher.stop()

    def _on_message(self, _channel, properties, delivery_tag, _body):
        correlation_id = properties.correlation_id

        self.consumer.acknowledge_message(properties, delivery_tag)

        if not self.auto_response_files:
            raise MqEndpointConsumerException(f'{self.name}: Auto-responses is empty')

        if self.index == len(self.auto_response_files):
            raise MqEndpointConsumerException(f'{self.name}: There are not enough auto-responses defined')

        with open(self.auto_response_files[self.index]) as f:
            body = json.load(f)
            self.publisher.publish_message(body, BasicProperties(app_id='Integration Test', content_type='application/json', correlation_id=correlation_id))
        self.index += 1

    def set_responses(self, auto_response_files=None):
        if auto_response_files is None:
            auto_response_files = []
        self.auto_response_files = auto_response_files
        self.index = 0
