import json
import logging

from pika import BasicProperties
from pika.adapters.blocking_connection import BlockingConnection
from response_exception import ResponseException


class HoppyClient:
    MAX_LATENCY = 30

    def __init__(self, connection: BlockingConnection, exchange: str, queue: str, reply_to: str):
        self.connection = connection
        self.channel = connection.channel()
        self.exchange = exchange
        self.queue = queue
        self.reply_to = reply_to

        self.channel.queue_declare(queue=self.queue, exclusive=True)
        self.channel.queue_bind(queue=self.queue, exchange=exchange)
        self.channel.queue_declare(queue=self.reply_to, exclusive=True)
        self.channel.queue_bind(queue=self.reply_to, exchange=exchange)

        self.channel.basic_consume(
            queue=self.reply_to,
            on_message_callback=self.on_reply,
            auto_ack=True)

        self.response = None
        self.correlation_id = None

    def request(self, body):
        self.response = None

        # TODO replace with generated correlation_id
        self.correlation_id = "1"
        logging.info(f"event=requestStarted queue={self.queue} correlation_id={self.correlation_id} requestBody={body}")

        self.channel.basic_publish(exchange=self.exchange,
                                   routing_key=self.queue,
                                   properties=BasicProperties(
                                       content_type="application/json",
                                       reply_to=self.reply_to,
                                       correlation_id=self.correlation_id),
                                   body=json.dumps(body))
        try:
            self.connection.process_data_events(time_limit=self.MAX_LATENCY)
        except json.JSONDecodeError:
            logging.error(f"event=invalidResponseJson request={body}")
            raise ResponseException("Could not decode response. Invalid json.", self.correlation_id)

        if not self.response:
            logging.error(f"event=noResponse request={body}")
            raise ResponseException("Request timed out", self.correlation_id)

        logging.info(
            f"event=requestCompleted queue={self.queue} correlation_id={self.correlation_id} response={self.response}")

        return self.response

    def on_reply(self, ch, method, props, body):
        if self.correlation_id == props.correlation_id:
            self.response = json.loads(body)
