import asyncio
import functools
import logging
import time

import pika
from pika.adapters.asyncio_connection import AsyncioConnection


class AsyncConsumer(object):
    def __init__(self, exchange, exchange_type, queue, routing_key,
                 connection_parameters: pika.ConnectionParameters,
                 reply_callback):
        self.exchange = exchange
        self.exchange_type = exchange_type
        self.queue = queue
        self.routing_key = routing_key
        self.connection_parameters = connection_parameters

        self.should_reconnect = False
        self.was_consuming = False

        self._connection = None
        self._channel = None
        self._closing = False
        self._consumer_tag = None
        self._consuming = False
        # In production, experiment with higher prefetch values
        # for higher consumer throughput
        self._prefetch_count = 1
        self.reply_callback = reply_callback

    def connect(self, loop):
        logging.info(f'Consumer - Connecting to RabbitMq params={self.connection_parameters}')
        return AsyncioConnection(
            parameters=self.connection_parameters,
            on_open_callback=self.on_connection_open,
            on_open_error_callback=self.on_connection_open_error,
            on_close_callback=self.on_connection_closed,
            custom_ioloop=loop)

    def close_connection(self):
        self._consuming = False
        if self._connection.is_closing or self._connection.is_closed:
            logging.info('Consumer - Connection is closing or already closed')
        else:
            logging.info('Consumer - Closing connection')
            self._connection.close()

    def on_connection_open(self, connection):
        self._connection = connection
        logging.info('Consumer - Connection opened')
        self.open_channel()

    def on_connection_open_error(self, _unused_connection, err):
        logging.error('Consumer - Connection open failed: %s', err)
        self.reconnect()

    def on_connection_closed(self, _unused_connection, reason):
        self._channel = None
        if self._closing:
            self._connection.ioloop.stop()
        else:
            logging.warning(f'Consumer - Connection closed, reconnect necessary: {reason}')
            self.reconnect()

    def reconnect(self):
        self.should_reconnect = True
        self.stop()

    def open_channel(self):
        logging.info('Consumer - Creating a new channel')
        self._connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        logging.info('Consumer - Channel opened')
        self._channel = channel
        self._channel.add_on_close_callback(self.on_channel_closed)
        self.setup_exchange(self.exchange)

    def on_channel_closed(self, channel, reason):
        logging.warning(f'Consumer - Channel {channel} was closed: {reason}')
        self.close_connection()

    def setup_exchange(self, exchange_name):
        logging.info(f'Consumer - Declaring exchange: {self.exchange}')
        self._channel.exchange_declare(
            exchange=exchange_name,
            exchange_type=self.exchange_type,
            durable=True,
            auto_delete=True,
            callback=self.on_exchange_declare_ok)

    def on_exchange_declare_ok(self, _unused_frame):
        logging.info(f'Consumer - Exchange declared: {self.exchange}')
        self.setup_queue()

    def setup_queue(self):
        logging.info(f'Consumer - Declaring queue {self.queue}')
        self._channel.queue_declare(queue=self.queue, callback=self.on_queue_declare_ok)

    def on_queue_declare_ok(self, _unused_frame):
        logging.info(f'Consumer - Binding {self.exchange} to {self.queue} with {self.routing_key}')
        self._channel.queue_bind(
            self.queue,
            self.exchange,
            routing_key=self.routing_key,
            callback=self.on_bind_ok)

    def on_bind_ok(self, _unused_frame):
        logging.info(f'Consumer - Queue bound: {self.queue}')
        self._channel.basic_qos(prefetch_count=self._prefetch_count, callback=self.on_basic_qos_ok)

    def on_basic_qos_ok(self, _unused_frame):
        logging.info(f'Consumer - QOS set to: {self._prefetch_count}')
        self.start_consuming()

    def start_consuming(self):
        logging.info('Consumer - Issuing consumer related RPC commands')
        self._channel.add_on_cancel_callback(self.on_consumer_cancelled)
        self._consumer_tag = self._channel.basic_consume(self.queue, self.on_message)
        self.was_consuming = True
        self._consuming = True

    def on_consumer_cancelled(self, method_frame):
        logging.info(f'Consumer - Consumer was cancelled remotely, shutting down: {method_frame}')
        if self._channel:
            self._channel.close()

    def on_message(self, _unused_channel, basic_deliver, properties, body):
        logging.info(f'Consumer - Received message # {basic_deliver.delivery_tag} from {properties.app_id}: {body}')
        self.reply_callback(self._channel,
                            properties,
                            basic_deliver.delivery_tag,
                            body)

    def acknowledge_message(self, delivery_tag):
        logging.info('Consumer - Acknowledging message %s', delivery_tag)
        self._channel.basic_ack(delivery_tag)

    def reject_message(self, delivery_tag, requeue=True):
        logging.info('Consumer - Rejecting message {delivery_tag}')
        self._channel.basic_reject(delivery_tag, requeue)

    def stop_consuming(self):
        if self._channel:
            logging.info('Consumer - Sending a Basic.Cancel RPC command to RabbitMQ')
            cb = functools.partial(
                self.on_cancel_ok, userdata=self._consumer_tag)
            self._channel.basic_cancel(self._consumer_tag, cb)

    def on_cancel_ok(self, _unused_frame, userdata):
        self._consuming = False
        logging.info('Consumer - RabbitMQ acknowledged the cancellation of the consumer: %s', userdata)
        self.close_channel()

    def close_channel(self):
        logging.info('Consumer - Closing the channel')
        self._channel.close()

    def stop(self):
        if not self._closing:
            self._closing = True
            logging.info('Consumer - Stopping Async Consumer')
            if self._consuming:
                self.stop_consuming()
            logging.info('Consumer - Stopped Async Consumer')


class ReconnectingConsumer(object):
    """This is an example consumer that will reconnect if the nested
    ExampleConsumer indicates that a reconnect is necessary.

    """

    def __init__(self, exchange, exchange_type, queue, routing_key, connection_parameters: pika.ConnectionParameters):
        self.exchange = exchange
        self.exchange_type = exchange_type
        self.queue = queue
        self.routing_key = routing_key
        self.connection_parameters = connection_parameters

        self._reconnect_delay = 0
        self._consumer = AsyncConsumer(exchange, exchange_type, queue, routing_key, connection_parameters)

    def run(self):
        while True:
            try:
                self._consumer.connect(asyncio.get_running_loop())
            except KeyboardInterrupt:
                self._consumer.stop()
                break
            self._maybe_reconnect()

    def _maybe_reconnect(self):
        if self._consumer.should_reconnect:
            self._consumer.stop()
            reconnect_delay = self._get_reconnect_delay()
            logging.info('Reconnecting after %d seconds', reconnect_delay)
            time.sleep(reconnect_delay)
            self._consumer = AsyncConsumer(self.exchange, self.exchange_type, self.queue, self.routing_key,
                                           self.connection_parameters)

    def _get_reconnect_delay(self):
        if self._consumer.was_consuming:
            self._reconnect_delay = 0
        else:
            self._reconnect_delay += 1
        if self._reconnect_delay > 30:
            self._reconnect_delay = 30
        return self._reconnect_delay
