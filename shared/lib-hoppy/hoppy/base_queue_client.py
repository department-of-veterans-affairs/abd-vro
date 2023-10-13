import logging
import time
from abc import ABC, abstractmethod
from enum import StrEnum, auto

from hoppy.config import RABBITMQ_CONFIG
from hoppy.hoppy_properties import ExchangeProperties, QueueProperties
from pika import ConnectionParameters, PlainCredentials
from pika.adapters.asyncio_connection import AsyncioConnection


class Type(StrEnum):
    CONSUMER = auto()
    PUBLISHER = auto()

    def __str__(self):
        return self.value


class BaseQueueClient(ABC):
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
                 _client_type: Type,
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

        self._client_type = _client_type
        if config is None:
            config = {}
        self.config = {**RABBITMQ_CONFIG, **config}
        self.connection_parameters = self._create_connection_parameters()

        self.exchange_name = exchange_properties.name
        self.exchange_type = exchange_properties.type
        self.passive_declare_exchange = exchange_properties.passive_declare
        self.durable_exchange = exchange_properties.durable
        self.auto_delete_exchange = exchange_properties.auto_delete

        self.queue_name = queue_properties.name
        self.passive_declare_queue = queue_properties.passive_declare
        self.durable_queue = queue_properties.durable
        self.auto_delete_queue = queue_properties.auto_delete
        self.exclusive_queue = queue_properties.exclusive

        self.routing_key = routing_key

        self._loop = None
        self._connection = None
        self._channel = None
        self._max_reconnect_delay = self.config.get('max_reconnect_delay', 30)
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)
        self._stopping = False

    def _create_connection_parameters(self) -> ConnectionParameters:
        credentials = PlainCredentials(self.config["username"], self.config["password"])
        return ConnectionParameters(
            host=self.config['host'],
            port=self.config['port'],
            credentials=credentials)

    def _initialize_connection_session(self):
        """The following attributes are used per connection session. When a reconnect happens, they should be reset."""
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)
        self._stopping = False

    def connect(self, loop=None):
        """
        Creates the asyncio connection to RabbitMQ

        Parameters
        ----------
        loop = None | asyncio.AbstractEventLoop | nbio_interface.AbstractIOServices
            Defaults to asyncio.get_event_loop()
        """

        self._loop = loop

        logging.debug(f'event=connectingToRabbitMq client_type={self._client_type} config={self.config}')
        self._connection = AsyncioConnection(
            parameters=self.connection_parameters,
            on_open_callback=self._on_connection_open,
            on_open_error_callback=self._on_connection_open_error,
            on_close_callback=self._on_connection_closed,
            custom_ioloop=loop)
        return self._connection

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
            logging.debug(f'event=stopping client_type={self._client_type}')
            self._shut_down()
            logging.debug(f'event=stopped client_type={self._client_type}')

    def _on_connection_open(self, connection):
        logging.debug(f'event=openedConnection client_type={self._client_type}')
        self._connection = connection
        self._initialize_connection_session()
        self._open_channel()

    def _on_connection_open_error(self, _unused_connection, err):
        logging.error(f'event=failedToOpenConnection client_type={self._client_type} err={err}')
        self._reconnect()

    def _on_connection_closed(self, _unused_connection, reason):
        self._channel = None
        if self._stopping:
            self._connection.ioloop.stop()
            logging.debug(f'event=closedConnection '
                          f'client_type={self._client_type} '
                          f'closing={self._connection.is_closing} '
                          f'closed={self._connection.is_closed}')
        else:
            logging.warning(f'event=connectionClosedUnexpectedly client_type={self._client_type} reason={reason}')
            self._reconnect()

    def _close_connection(self):
        if self._connection is not None:
            logging.debug(f'event=closingConnection '
                          f'client_type={self._client_type} '
                          f'closing={self._connection.is_closing} '
                          f'closed={self._connection.is_closed}')
            if not self._connection.is_closing and not self._connection.is_closed:
                self._connection.close()

    def _reconnect(self):
        self.stop()
        reconnect_delay = self._get_reconnect_delay()
        logging.warning(f'event=reconnecting client_type={self._client_type} reconnect_delay_seconds={reconnect_delay}')
        time.sleep(reconnect_delay)
        self.connect(self._loop)

    def _get_reconnect_delay(self):
        self._reconnect_delay += 1
        if self._reconnect_delay > self._max_reconnect_delay:
            self._reconnect_delay = self._max_reconnect_delay
        return self._reconnect_delay

    def _open_channel(self):
        logging.debug(f'event=openingChannel client_type={self._client_type}')
        self._connection.channel(on_open_callback=self._on_channel_open)

    def _on_channel_open(self, channel):
        logging.debug(f'event=openedChannel client_type={self._client_type} channel={channel}')
        self._channel = channel
        self._channel.add_on_close_callback(self._on_channel_closed)
        self._setup_exchange()

    def _on_channel_closed(self, channel, reason):
        logging.warning(f'event=closedChannel client_type={self._client_type} channel={channel} reason={reason}')
        self._close_connection()

    def _close_channel(self):
        if self._channel is not None:
            logging.debug(f'event=closingChannel client_type={self._client_type} channel={self._channel}')
            self._channel.close()

    def _setup_exchange(self):
        logging.debug(f'event=declaringExchange '
                      f'client_type={self._client_type} '
                      f'exchange={self.exchange_name} '
                      f'type={self.exchange_type} '
                      f'passive_declare={self.passive_declare_exchange} '
                      f'durable={self.durable_exchange} '
                      f'auto_delete={self.auto_delete_exchange}')
        self._channel.exchange_declare(exchange=self.exchange_name,
                                       exchange_type=self.exchange_type,
                                       passive=self.passive_declare_exchange,
                                       durable=self.durable_exchange,
                                       auto_delete=self.auto_delete_exchange,
                                       callback=self._on_exchange_declare_ok)

    def _on_exchange_declare_ok(self, _unused_frame):
        logging.debug(f'event=declaredExchange '
                      f'client_type={self._client_type} '
                      f'exchange={self.exchange_name} '
                      f'type={self.exchange_type} '
                      f'passive_declare={self.passive_declare_exchange} '
                      f'durable={self.durable_exchange} '
                      f'auto_delete={self.auto_delete_exchange}')
        self._setup_queue()

    def _setup_queue(self):
        logging.debug(f'event=declaringQueue '
                      f'client_type={self._client_type} '
                      f'queue={self.queue_name} '
                      f'passive_declare={self.passive_declare_queue} '
                      f'durable={self.durable_queue} '
                      f'exclusive={self.exclusive_queue} '
                      f'auto_delete={self.auto_delete_exchange}')
        self._channel.queue_declare(queue=self.queue_name,
                                    passive=self.passive_declare_queue,
                                    durable=self.durable_queue,
                                    exclusive=self.exclusive_queue,
                                    auto_delete=self.auto_delete_queue,
                                    callback=self._on_queue_declare_ok)

    def _on_queue_declare_ok(self, _unused_frame):
        logging.debug(f'event=declaredQueue '
                      f'client_type={self._client_type} '
                      f'queue={self.queue_name} '
                      f'passive_declare={self.passive_declare_queue} '
                      f'durable={self.durable_queue} '
                      f'exclusive={self.exclusive_queue} '
                      f'auto_delete={self.auto_delete_exchange}')
        logging.debug(f'event=bindingQueue '
                      f'client_type={self._client_type} '
                      f'queue={self.queue_name} '
                      f'exchange={self.exchange_name} '
                      f'routing_key={self.routing_key}')
        self._channel.queue_bind(self.queue_name,
                                 self.exchange_name,
                                 routing_key=self.routing_key,
                                 callback=self._on_bind_ok)

    def _on_bind_ok(self, _unused_frame):
        logging.debug(f'event=boundQueue '
                      f'client_type={self._client_type} '
                      f'queue={self.queue_name} '
                      f'exchange={self.exchange_name} '
                      f'routing_key={self.routing_key}')
        self._ready()
