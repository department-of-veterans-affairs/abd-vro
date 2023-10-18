import json

from hoppy.base_queue_client import BaseQueueClient, ClientType
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties
from pika.spec import BasicProperties


class AsyncPublisher(BaseQueueClient):
    """Creates an asynchronous publisher that can be used to publish messages to a queue"""

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

        super().__init__(ClientType.PUBLISHER, config, exchange_properties, queue_properties, routing_key)

        self._deliveries = {}
        self._acked = 0
        self._nacked = 0
        self._rejected = 0
        self._message_number = 0

    def _initialize_connection_session(self):
        """The following attributes are used per connection session. When a reconnect happens, they should be reset."""

        super()._initialize_connection_session()

        self._deliveries = {}
        self._acked = 0
        self._nacked = 0
        self._rejected = 0
        self._message_number = 0

    def _ready(self):
        """Executed when the exchange and queue are ready and this class can start the process of consuming.
        Overrides super class abstract method."""

        self._debug('enabledDeliveryConfirmation')
        self._channel.confirm_delivery(self._on_delivery_confirmation)

    def _shut_down(self):
        """Called when the client is requested to stop.
        Overrides super class abstract method"""

        self._close_channel()
        self._close_connection()

    def _on_delivery_confirmation(self, method_frame):
        confirmation_type = method_frame.method.NAME.split('.')[1].lower()
        ack_multiple = method_frame.method.multiple
        delivery_tag = method_frame.method.delivery_tag

        if confirmation_type == 'ack':
            self._acked += 1
        elif confirmation_type == 'nack':
            self._nacked += 1
        elif confirmation_type == 'reject':
            self._rejected += 1

        del self._deliveries[delivery_tag]

        if ack_multiple:
            for tmp_tag in list(self._deliveries):
                if tmp_tag <= delivery_tag:
                    self._acked += 1
                    del self._deliveries[tmp_tag]

        self._debug('receivedDeliveryConfirmation',
                    confirmation_type=confirmation_type,
                    delivery_tag=delivery_tag,
                    ack_multiple=ack_multiple,
                    total=self._message_number,
                    unconfirmed=len(self._deliveries),
                    acked=self._acked,
                    nacked=self._nacked,
                    rejected=self._rejected)

    def publish_message(self, message='hello', properties: BasicProperties = None):
        """Publishes a message to the queue"""

        if self._channel is None or not self._channel.is_open:
            self._warning('publishMessageFailed',
                          channel=self._channel)
            return

        self._channel.basic_publish(self.exchange_name, self.routing_key,
                                    json.dumps(message, ensure_ascii=False),
                                    properties)
        self._message_number += 1
        self._deliveries[self._message_number] = True
        self._debug('publishedMessage',
                    message_number=self._message_number)
