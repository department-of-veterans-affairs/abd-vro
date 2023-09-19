import functools
import json
import logging

import pika
from pika.adapters.asyncio_connection import AsyncioConnection


class AsyncPublisher(object):
    def __init__(self, exchange, exchange_type, queue, routing_key, connection_parameters: pika.ConnectionParameters):
        """Set up the example publisher object, passing in the URL we will use to connect to RabbitMQ."""
        self.exchange = exchange
        self.exchange_type = exchange_type
        self.queue = queue
        self.routing_key = routing_key
        self.connection_parameters = connection_parameters

        self._connection = None
        self._channel = None

        self._deliveries = {}
        self._acked = 0
        self._nacked = 0
        self._message_number = 0

        self._stopping = False

    def connect(self, loop):
        logging.info(f'Publisher -  Connecting to RabbitMq params={self.connection_parameters}')
        self._connection = AsyncioConnection(
            parameters=self.connection_parameters,
            on_open_callback=self.on_connection_open,
            on_open_error_callback=self.on_connection_open_error,
            on_close_callback=self.on_connection_closed,
            custom_ioloop=loop)
        return self._connection

    def on_connection_open(self, connection):
        logging.info('Publisher -  Connection opened')
        self._connection = connection
        self.open_channel()

    def on_connection_open_error(self, _unused_connection, err):
        logging.error(f'Publisher -  Connection open failed, reopening in 5 seconds: {err}')
        self._connection.ioloop.call_later(5, self._connection.ioloop.stop)

    def on_connection_closed(self, _unused_connection, reason):
        self._channel = None
        logging.warning('Publisher -  Channel Closed')

    def open_channel(self):
        logging.info('Publisher -  Creating a new channel')
        self._connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        logging.info('Publisher -  Channel opened')
        self._channel = channel
        self.add_on_channel_close_callback()
        self.setup_exchange(self.exchange)

    def add_on_channel_close_callback(self):
        logging.info('Publisher -  Adding channel close callback')
        self._channel.add_on_close_callback(self.on_channel_closed)

    def on_channel_closed(self, channel, reason):
        logging.warning(f'Publisher -  Channel {channel} was closed: {reason}')
        self._channel = None
        if not self._stopping:
            self._connection.close()

    def setup_exchange(self, exchange_name):
        logging.info(f'Publisher -  Declaring exchange {exchange_name}')
        cb = functools.partial(self.on_exchange_declare_ok,
                               userdata=exchange_name)
        self._channel.exchange_declare(exchange=exchange_name,
                                       exchange_type=self.exchange_type,
                                       durable=True,
                                       auto_delete=True,
                                       callback=cb)

    def on_exchange_declare_ok(self, _unused_frame, userdata):
        logging.info(f'Publisher -  Exchange declared {userdata}')
        self.setup_queue(self.queue)

    def setup_queue(self, queue_name):
        logging.info(f'Publisher -  Declaring queue {queue_name}', )
        self._channel.queue_declare(queue=queue_name,
                                    callback=self.on_queue_declare_ok)

    def on_queue_declare_ok(self, _unused_frame):
        logging.info(f'Publisher -  Binding {self.exchange} to {self.queue} with {self.routing_key}')
        self._channel.queue_bind(self.queue,
                                 self.exchange,
                                 routing_key=self.routing_key,
                                 callback=self.on_bind_ok)

    def on_bind_ok(self, _unused_frame):
        logging.info(f'Publisher -  Queue bound {self.queue}')
        self.start_publishing()

    def start_publishing(self):
        logging.info('Publisher -  Issuing Confirm.Select RPC command')
        self._channel.confirm_delivery(self.on_delivery_confirmation)

    def on_delivery_confirmation(self, method_frame):
        confirmation_type = method_frame.method.NAME.split('.')[1].lower()
        ack_multiple = method_frame.method.multiple
        delivery_tag = method_frame.method.delivery_tag

        logging.info(
            f'Publisher -  Received {confirmation_type} for delivery tag: {delivery_tag} (multiple: {ack_multiple})')

        if confirmation_type == 'ack':
            self._acked += 1
        elif confirmation_type == 'nack':
            self._nacked += 1

        del self._deliveries[delivery_tag]

        if ack_multiple:
            for tmp_tag in list(self._deliveries.keys()):
                if tmp_tag <= delivery_tag:
                    self._acked += 1
                    del self._deliveries[tmp_tag]

        logging.info(
            f'Publisher -  Published {self._message_number} messages, '
            f'{len(self._deliveries)} have yet to be confirmed, '
            f'{self._acked} were acked and {self._nacked} were nacked')

    def publish_message(self, message='hello', properties: pika.BasicProperties = None):
        if self._channel is None or not self._channel.is_open:
            logging.warning(f'Publisher -  Could not publish message with channel={self._channel}')
            return

        self._channel.basic_publish(self.exchange, self.routing_key,
                                    json.dumps(message, ensure_ascii=False),
                                    properties)
        self._message_number += 1
        self._deliveries[self._message_number] = True
        logging.info(f'Publisher -  Published message # {self._message_number}', )

    def stop(self):
        logging.info('Publisher - Stopping Async Publisher')
        self._stopping = True
        self.close_channel()
        self.close_connection()
        logging.info('Publisher - Stopped Async Publisher')

    def close_channel(self):
        if self._channel is not None:
            logging.info('Publisher - Closing the channel')
            self._channel.close()

    def close_connection(self):
        if self._connection is not None:
            logging.info('Publisher - Closing connection')
            self._connection.close()
