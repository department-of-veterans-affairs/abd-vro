import logging
import time
from abc import ABC, abstractmethod

from hoppy.config import RABBITMQ_CONFIG
from pika.adapters.asyncio_connection import AsyncioConnection
from pika import ConnectionParameters, PlainCredentials


class BaseChannelOpener(ABC):
    def __init__(self, config: [dict | None] = None):
        if config is None:
            config = {}
        self.config = {**RABBITMQ_CONFIG, **config}
        self.connection_parameters = self._create_connection_parameters()

        self._connection = None
        self._channel = None
        self._max_reconnect_delay = self.config.get('max_reconnect_delay', 30)
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)
        self._custom_loop = None

    def _create_connection_parameters(self) -> ConnectionParameters:
        credentials = PlainCredentials(self.config["username"], self.config["password"])
        return ConnectionParameters(
            host=self.config['host'],
            port=self.config['port'],
            virtual_host=self.config['virtual_host'],
            credentials=credentials)

    def _initialize_connection_session(self):
        """The following attributes are used per connection session. When a reconnect happens, they should be reset."""
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)

    def connect(self, loop=None):
        """
        Creates the asyncio connection to RabbitMQ

        Parameters
        ----------
        loop = None | asyncio.AbstractEventLoop | nbio_interface.AbstractIOServices
            Defaults to asyncio.get_event_loop()
        """

        self._custom_loop = loop

        self._info('connectingToRabbitMq', config=self.config)
        self._connection = AsyncioConnection(
            parameters=self.connection_parameters,
            on_open_callback=self._on_connection_open,
            on_open_error_callback=self._on_connection_open_error,
            on_close_callback=self._on_connection_closed,
            custom_ioloop=loop)
        return self._connection

    def _on_connection_open(self, connection):
        self._debug('openedConnection')
        self._connection = connection
        self._initialize_connection_session()
        self._open_channel()

    def _on_connection_open_error(self, _unused_connection, err):
        self._error('failedToOpenConnection', err)
        self._reconnect()

    def _close_connection(self):
        if self._connection is not None:
            self._debug('closingConnection',
                        closing=self._connection.is_closing,
                        closed=self._connection.is_closed)
            if not self._connection.is_closing and not self._connection.is_closed:
                self._connection.close()

    @abstractmethod
    def _on_connection_closed(self, _unused_connection, reason):
        pass

    def _reconnect(self):
        self.stop()
        reconnect_delay = self._get_reconnect_delay()
        self._warning('reconnecting', reconnect_delay_seconds=reconnect_delay)
        time.sleep(reconnect_delay)
        self.connect(self._custom_loop)

    def _get_reconnect_delay(self):
        self._reconnect_delay += 1
        if self._reconnect_delay > self._max_reconnect_delay:
            self._reconnect_delay = self._max_reconnect_delay
        return self._reconnect_delay

    def _open_channel(self):
        self._debug('openingChannel')
        self._connection.channel(on_open_callback=self._on_channel_open)

    def _on_channel_closed(self, channel, reason):
        self._warning('closedChannel', channel=channel, reason=reason)
        self._close_connection()

    def _close_channel(self):
        if self._channel is not None:
            self._debug('closingChannel', channel=self._channel)
            self._channel.close()

    def _on_channel_open(self, channel):
        self._debug('openedChannel', channel=channel)
        self._channel = channel
        self._channel.add_on_close_callback(self._on_channel_closed)

    @staticmethod
    def __kwarg_str(**kwargs):
        return ' '.join(f'{k}={v}' for k, v in kwargs.items())

    def _debug(self, event, **kwargs):
        msg = f'event={event} client_type={self._client_type.name} {self.__kwarg_str(**kwargs)}'
        logging.debug(msg)

    def _info(self, event, **kwargs):
        msg = f'event={event} client_type={self._client_type.name} {self.__kwarg_str(**kwargs)}'
        logging.info(msg)

    def _warning(self, event, **kwargs):
        msg = f'event={event} client_type={self._client_type.name} {self.__kwarg_str(**kwargs)}'
        logging.warning(msg)

    def _error(self, event, error=None, **kwargs):
        msg = f'event={event} client_type={self._client_type.name} err={error!r} {self.__kwarg_str(**kwargs)}'
        logging.error(msg)
