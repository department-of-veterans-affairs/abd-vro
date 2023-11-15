from abc import abstractmethod
from enum import Enum

from hoppy.base_queue_declarer import BaseQueueDeclarer
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties
from pika import ConnectionParameters, PlainCredentials

ClientType = Enum('ClientType', ['CONSUMER', 'PUBLISHER', 'CHANNEL_ONLY'])


class BaseQueueClient(BaseQueueDeclarer):
    """
    Creates the base of an asynchronous client for connecting to an exchange and queue. This class does not implement
    publishing or consuming from the queues, but merely handles creating the connection, declaring the exchange,
    declaring the queue, adding callbacks for connection and channel events (open/error/close).

    Abstract Functions
    -------------------
    _ready - Method to be called after the asyncio connection has successfully opened the connection, created the
        channel, declared the exchange, declared the queue, and bound the queue and exchange
    _shut_down - Method to be called as part of the self.stop(), this method should do the processing needed to shut
        down the client
    """

    def __init__(self,
                 _client_type: ClientType,
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
        super().__init__(config, exchange_properties, queue_properties, routing_key)

        self._client_type = _client_type

        self._is_ready = False
        self._stopping = False
        self._stopped = False

    def _initialize_connection_session(self):
        """The following attributes are used per connection session. When a reconnect happens, they should be reset."""
        super()._initialize_connection_session()
        self._is_ready = False
        self._stopping = False
        self._stopped = False

    @property
    def is_ready(self) -> bool:
        return self._is_ready

    @property
    def is_ready(self) -> bool:
        return self._is_ready

    @abstractmethod
    def _ready(self):
        """Method to be called after the asyncio connection has successfully opened the connection, created the
        channel, declared the exchange, declared the queue, and bound the queue and exchange"""

        pass

    @abstractmethod
    def _shut_down(self):
        """Method to be called as part of the self.stop(), this method should do the processing needed to shut
        down the client"""

        pass

    def stop(self):
        """Calls the abstract method self._shut_down() meant to perform the processing needed to stop the connection"""

        if not self._stopping:
            self._stopping = True
            self._debug('stopping')
            self._shut_down()
            self._debug('stopped')

    @property
    def is_stopped(self) -> bool:
        return self._stopped

    def _on_connection_closed(self, _unused_connection, reason):
        self._channel = None
        if self._stopping:
            if not self._custom_loop:
                self._connection.ioloop.stop()
            self._debug('closedConnection',
                        closing=self._connection.is_closing,
                        closed=self._connection.is_closed)
            self._stopped = True
        else:
            self._warning('connectionClosedUnexpectedly', reason=reason)
            self._reconnect()

    def _on_exchange_declare_ok(self, _unused_frame):
        super()._on_exchange_declare_ok(_unused_frame)
        self._setup_queue()

    def _on_bind_ok(self, _unused_frame):
        super()._on_bind_ok(_unused_frame)
        self._ready()
