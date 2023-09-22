import functools
import logging
import time
from typing import Callable

from hoppy.config import RABBITMQ_CONFIG
from hoppy.util import create_connection_parameters
from pika.adapters.asyncio_connection import AsyncioConnection


class AsyncConsumer(object):
    def __init__(self,
                 config: [dict | None] = None,
                 exchange: str = '',
                 exchange_type: str = 'direct',
                 queue: str = '',
                 routing_key: str = '',
                 reply_callback: Callable = None):
        if config is None:
            config = {}
        self.config = {**RABBITMQ_CONFIG, **config}
        self.connection_parameters = create_connection_parameters(self.config)
        self.exchange = exchange
        self.exchange_type = exchange_type
        self.queue = queue
        self.routing_key = routing_key

        self._loop = None
        self._connection = None
        self._channel = None
        self._consumer_tag = None
        self._max_reconnect_delay = self.config.get('max_reconnect_delay', 30)

        # In production, experiment with higher prefetch values
        # for higher consumer throughput
        self._prefetch_count = 1
        self.reply_callback = reply_callback

        # The following attributes are used per connection session. When a reconnect happens, they should be reset.
        # See self.initialize_connection_session()
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)
        self._stopping = False
        self._consuming = False

    def initialize_connection_session(self):
        self._reconnect_delay = self.config.get('initial_reconnect_delay', 0)
        self._stopping = False
        self._consuming = False

    def connect(self, loop=None):
        self._loop = loop

        logging.debug(f'Consumer - Connecting to RabbitMq params={self.connection_parameters}')
        return AsyncioConnection(
            parameters=self.connection_parameters,
            on_open_callback=self.on_connection_open,
            on_open_error_callback=self.on_connection_open_error,
            on_close_callback=self.on_connection_closed,
            custom_ioloop=loop)

    def close_connection(self):
        self._consuming = False
        if self._connection.is_closing or self._connection.is_closed:
            logging.debug('Consumer - Connection is closing or already closed')
        else:
            logging.debug('Consumer - Closing connection')
            self._connection.close()

    def on_connection_open(self, connection):
        self._connection = connection
        logging.debug('Consumer - Connection opened')
        self.initialize_connection_session()
        self.open_channel()

    def on_connection_open_error(self, _unused_connection, err):
        logging.error('Consumer - Connection open failed: %s', err)
        self.reconnect()

    def on_connection_closed(self, _unused_connection, reason):
        self._channel = None
        if self._stopping:
            self._connection.ioloop.stop()
        else:
            logging.warning(f'Consumer - Connection closed, reason: {reason}')
            self.reconnect()

    def reconnect(self):
        self.stop()
        reconnect_delay = self._get_reconnect_delay()
        logging.warning('Reconnecting after %d seconds', reconnect_delay)
        time.sleep(reconnect_delay)
        self.connect(self._loop)

    def _get_reconnect_delay(self):
        self._reconnect_delay += 1
        if self._reconnect_delay > self._max_reconnect_delay:
            self._reconnect_delay = self._max_reconnect_delay
        return self._reconnect_delay

    def open_channel(self):
        logging.debug('Consumer - Creating a new channel')
        self._connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        logging.debug('Consumer - Channel opened')
        self._channel = channel
        self._channel.add_on_close_callback(self.on_channel_closed)
        self.setup_exchange(self.exchange)

    def on_channel_closed(self, channel, reason):
        logging.warning(f'Consumer - Channel {channel} was closed: {reason}')
        self.close_connection()

    def setup_exchange(self, exchange_name):
        logging.debug(f'Consumer - Declaring exchange: {self.exchange}')
        self._channel.exchange_declare(
            exchange=exchange_name,
            exchange_type=self.exchange_type,
            durable=True,
            auto_delete=True,
            callback=self.on_exchange_declare_ok)

    def on_exchange_declare_ok(self, _unused_frame):
        logging.debug(f'Consumer - Exchange declared: {self.exchange}')
        self.setup_queue()

    def setup_queue(self):
        logging.debug(f'Consumer - Declaring queue {self.queue}')
        self._channel.queue_declare(queue=self.queue, callback=self.on_queue_declare_ok)

    def on_queue_declare_ok(self, _unused_frame):
        logging.debug(f'Consumer - Binding {self.exchange} to {self.queue} with {self.routing_key}')
        self._channel.queue_bind(
            self.queue,
            self.exchange,
            routing_key=self.routing_key,
            callback=self.on_bind_ok)

    def on_bind_ok(self, _unused_frame):
        logging.debug(f'Consumer - Queue bound: {self.queue}')
        self._channel.basic_qos(prefetch_count=self._prefetch_count, callback=self.on_basic_qos_ok)

    def on_basic_qos_ok(self, _unused_frame):
        logging.debug(f'Consumer - QOS set to: {self._prefetch_count}')
        self.start_consuming()

    def start_consuming(self):
        logging.debug('Consumer - Issuing consumer related RPC commands')
        self._channel.add_on_cancel_callback(self.on_consumer_cancelled)
        self._consumer_tag = self._channel.basic_consume(self.queue, self.on_message)
        self._consuming = True

    def on_consumer_cancelled(self, method_frame):
        logging.debug(f'Consumer - Consumer was cancelled remotely, shutting down: {method_frame}')
        if self._channel:
            self._channel.close()

    def on_message(self, _unused_channel, basic_deliver, properties, body):
        logging.debug(f'Consumer - Received message # {basic_deliver.delivery_tag} from {properties.app_id}: {body}')
        self.reply_callback(self._channel,
                            properties,
                            basic_deliver.delivery_tag,
                            body)

    def acknowledge_message(self, delivery_tag):
        logging.debug('Consumer - Acknowledging message %s', delivery_tag)
        self._channel.basic_ack(delivery_tag)

    def reject_message(self, delivery_tag, requeue=True):
        logging.debug('Consumer - Rejecting message {delivery_tag}')
        self._channel.basic_reject(delivery_tag, requeue)

    def stop_consuming(self):
        if self._channel:
            logging.debug('Consumer - Sending a Basic.Cancel RPC command to RabbitMQ')
            cb = functools.partial(
                self.on_cancel_ok, userdata=self._consumer_tag)
            self._channel.basic_cancel(self._consumer_tag, cb)

    def on_cancel_ok(self, _unused_frame, userdata):
        self._consuming = False
        logging.debug('Consumer - RabbitMQ acknowledged the cancellation of the consumer: %s', userdata)
        self.close_channel()

    def close_channel(self):
        logging.debug('Consumer - Closing the channel')
        self._channel.close()

    def stop(self):
        if not self._stopping:
            self._stopping = True
            logging.debug('Consumer - Stopping Async Consumer')
            if self._consuming:
                self.stop_consuming()
            logging.debug('Consumer - Stopped Async Consumer')
