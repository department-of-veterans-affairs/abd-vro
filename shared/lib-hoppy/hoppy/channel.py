import asyncio
from hoppy.base_exchange_declarer import BaseExchangeDeclarer
from hoppy.base_channel_opener import BaseChannelOpener
from hoppy.base_queue_client import BaseQueueClient, ClientType
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties


class Channel(BaseQueueClient):
    """Creates a channel that can be used to declare exchanges and queues."""

    def __init__(self,
                 config: [dict | None] = None,
                 exchange_properties: ExchangeProperties = ExchangeProperties(),
                 queue_properties: QueueProperties = QueueProperties(),
                 routing_key: str = ''):
        """
        Creates this class

        :param config: dict | None = None
            collection of key value pairs used to create the RabbitMQ connection parameters (see pika.ConnectionParameters)
            this config is merged with the default RABBITMQ_CONFIG
        :param exchange_properties: ExchangeProperties
            properties dictating how the exchange is declared
        :param queue_properties: QueueProperties
            properties dictating how the queue is declared
        :param routing_key: str = ''
            the routing key used to route messages to the queue
        """

        super().__init__(ClientType.CHANNEL_ONLY, config, exchange_properties, queue_properties, routing_key)

    async def connect(self):
        super().connect()
        while not self.is_ready:
            await asyncio.sleep(0)

    def _ready(self):
        """Executed when the channel is open.
        Overrides super class abstract method."""

        self._is_ready = True

    def _shut_down(self):
        """Called when the client is requested to stop.
        Overrides super class abstract method"""

        self._close_channel()
        self._close_connection()

    def _on_channel_open(self, channel):
        BaseChannelOpener._on_channel_open(self, channel)
        self._ready()

    def exchange_declare(self, exchange_properties: ExchangeProperties = None):
        if exchange_properties:
            self._set_exchange_properties(exchange_properties)

        self._setup_exchange()

    def _on_exchange_declare_ok(self, _unused_frame):
        BaseExchangeDeclarer._on_exchange_declare_ok(self, _unused_frame)

    def queue_declare(self, queue_properties: QueueProperties = None, exchange_name: str = None, routing_key: str = None):
        if queue_properties:
            self._set_queue_properties(queue_properties)
        if exchange_name:
            self.exchange_name = exchange_name
        if routing_key:
            self.routing_key = routing_key

        self._setup_queue()
